package org.example.rushpurchase.service.impl

import cn.hutool.core.collection.CollectionUtil
import cn.hutool.core.date.DatePattern
import cn.hutool.core.date.DateUtil
import cn.hutool.core.io.file.FileNameUtil
import cn.hutool.core.util.IdUtil
import cn.hutool.crypto.digest.MD5
import cn.hutool.http.HttpRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.rushpurchase.cache.module.ZSetTuple
import org.example.rushpurchase.cache.service.StringRedisService
import org.example.rushpurchase.enums.FileStatus
import org.example.rushpurchase.minio.MinioConfigure
import org.example.rushpurchase.minio.helper.MinioHelper
import org.example.rushpurchase.mysql.entity.FileUploadInfo
import org.example.rushpurchase.service.FileChunkUploadInfoService
import org.example.rushpurchase.service.FileService
import org.example.rushpurchase.service.FileUploadInfoService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

@Service
class FileServiceImpl(
    val fileUploadInfoService: FileUploadInfoService,
    val fileChunkUploadInfoService: FileChunkUploadInfoService,
    val stringRedisService: StringRedisService,
    val minioHelper: MinioHelper,
    val minioConfigure: MinioConfigure,
) : FileService {

    private val log = KotlinLogging.logger {}

    override fun checkUploadFile(fileMd5: String): FileOperationResult {
        val result = FileOperationResult()
        val uploadInfoOptional = fileUploadInfoService.findFileUploadInfoByFileMd5(fileMd5)
        if (uploadInfoOptional.isEmpty) {
            result.fileStatus = FileStatus.UN_UPLOADED
            return result
        }
        val fileUploadInfo = uploadInfoOptional.get()
        if (fileUploadInfo.fileStatus == FileStatus.UPLOADED) {
            result.fileStatus = FileStatus.UPLOADED
            result.fileId = fileUploadInfo.id
            return result
        }
        val minioFileName = makeFilePath(fileUploadInfo.minioFileName)
        val chunkUploadedList = fileUploadInfo.uploadId?.let { minioHelper.listUploadChunkList(minioFileName, it) }
        result.fileStatus = FileStatus.UPLOADING
        result.chunkUploadedList = chunkUploadedList
        return result
    }

    override fun directUploadFile(multipartFile: MultipartFile): FileUploadInfo {
        val fileMd5 = MD5.create().digestHex(multipartFile.bytes)
        val uploadInfoOptional = fileUploadInfoService.findFileUploadInfoByFileMd5(fileMd5)
        return uploadInfoOptional.orElseGet {
            val timer = DateUtil.timer()
            val minioFileName =
                IdUtil.getSnowflakeNextIdStr() + "." + FileNameUtil.extName(multipartFile.originalFilename)
            val finalObjectsName = makeFilePath(minioFileName)
            try {
                minioHelper.putObject(finalObjectsName, multipartFile.inputStream).thenAccept { objectWriteResponse ->
                    log.info { "objectName：$minioFileName 上传成功，耗时：${timer.intervalPretty()}" }
                    fileUploadInfoService.saveFileFileStatus(fileMd5, FileStatus.UPLOADED)
                }
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
            val fileUploadInfo = FileUploadInfo()
            fileUploadInfo.fileMd5 = fileMd5
            fileUploadInfo.filePath = finalObjectsName
            fileUploadInfo.fileName = multipartFile.originalFilename
            fileUploadInfo.minioFileName = minioFileName
            fileUploadInfo.fileSize = multipartFile.size
            fileUploadInfo.totalChunk = 0
            fileUploadInfo.fileStatus = FileStatus.UN_UPLOADED
            fileUploadInfo.createdTime = LocalDateTime.now()
            // 保存文件上传信息
            fileUploadInfoService.saveFileUploadInfo(fileUploadInfo)
        }
    }

    override fun uploadFile(uploadInfoParam: UploadInfoParam): UploadInfo? {
        val fileUploadInfoOptional = fileUploadInfoService.findFileUploadInfoByFileMd5(uploadInfoParam.fileMd5)
        if (fileUploadInfoOptional.isPresent) {
            val fileUploadInfo = fileUploadInfoOptional.get()
            val partArr = fileUploadInfo.uploadId?.let {
                fileChunkUploadInfoService.findListByFileMd5AndUploadId(
                    uploadInfoParam.fileMd5,
                    it
                ).mapNotNull { fc -> fc.chunkNumber }
                    .toSet()
            }
            val finalMinioFileName = makeFilePath(fileUploadInfo.minioFileName)
            return getUploadInfo(finalMinioFileName, fileUploadInfo.uploadId!!, partArr!!)
        }
        val partCount = ceil(uploadInfoParam.fileSize / uploadInfoParam.chunkSize)
        log.info { "总分片数:$partCount" }
        val minioFileName = IdUtil.getSnowflakeNextIdStr() + "." + FileNameUtil.extName(uploadInfoParam.fileName)
        val finalObjectsName: String = makeFilePath(minioFileName)
        val uploadId: String? = minioHelper.initMultiPartUpload(finalObjectsName)

        return uploadId?.let {
            val fileUploadInfo = FileUploadInfo()
            fileUploadInfo.uploadId = uploadId
            fileUploadInfo.fileMd5 = uploadInfoParam.fileMd5
            fileUploadInfo.filePath = finalObjectsName
            fileUploadInfo.fileName = uploadInfoParam.fileName
            fileUploadInfo.minioFileName = minioFileName
            fileUploadInfo.totalChunk = partCount.toInt()
            fileUploadInfo.createdTime = LocalDateTime.now()
            fileUploadInfoService.saveFileUploadInfo(fileUploadInfo)
            fileChunkUploadInfoService.saveFileChunkUploadInfo(it, uploadInfoParam.fileMd5, partCount.toInt())
            val part = (0..partCount.toInt()).toSet()
            getUploadInfo(finalObjectsName, uploadId, part)
        }
    }

    override fun uploadFileChunk(multipartFile: MultipartFile, uploadId: String, chunkNumber: Int) {
        val forZSetValue = stringRedisService.getForZSetValue(uploadId, chunkNumber.toDouble())
        forZSetValue?.let {
            HttpRequest.put(it).body(multipartFile.bytes).execute().use { httpResponse ->
                if (httpResponse.status == 200) {
                    log.info { "上传分块完成" }
                    fileChunkUploadInfoService.updateChunkStatesToComplete(uploadId, chunkNumber)
                }
            }

        }
    }

    override fun mergeMultipartUpload(param: MergeMultipartParam): FileOperationResult {
        val result = FileOperationResult()
        val fileUploadInfo = fileUploadInfoService.findFileUploadInfoByFileMd5(param.fileMd5)
            .orElseThrow { RuntimeException("上传文件信息不存在,请联系管理员") }
        val fileUrl = minioHelper.mergeMultiPartUpload(fileUploadInfo.filePath!!, param.uploadId)
        fileUrl?.let {
            log.info { "合并后文件地址:$fileUrl" }
            val statObjectResponse = minioHelper.statObject(fileUploadInfo.filePath)
            statObjectResponse?.let { stat ->
                fileUploadInfo.fileStatus = FileStatus.UPLOADED
                fileUploadInfo.fileSize = stat.size()
                fileUploadInfoService.saveFileUploadInfo(fileUploadInfo)
            }
            fileUploadInfo.fileStatus = FileStatus.UPLOADED
            fileUploadInfo.id = fileUploadInfo.id
            fileUploadInfoService.saveFileUploadInfo(fileUploadInfo)
        }
        return result
    }

    override fun downloadFile(fileId: Long, request: HttpServletRequest, response: HttpServletResponse) {
        val uploadInfoOptional = fileUploadInfoService.findFileUploadInfoById(fileId)
        uploadInfoOptional.ifPresent { fileUploadInfo ->
            try {
                val userAgent = request.getHeader("User-Agent").uppercase(Locale.getDefault())
                response.setHeader(
                    "Content-disposition",
                    "attachment;fileName=" + handleFileName(userAgent, fileUploadInfo.fileName)
                )
                response.setHeader("Content-Length", fileUploadInfo.fileSize.toString())
                minioHelper.downloadObject(fileUploadInfo.filePath, response)
            } catch (e: IOException) {
                log.error { e.message }
            }
        }
    }

    override fun showFile(fileId: Long, request: HttpServletRequest, response: HttpServletResponse) {
        val fileUploadInfoOptional = fileUploadInfoService.findFileUploadInfoById(fileId)
        fileUploadInfoOptional.ifPresent { fileUploadInfo ->
            minioHelper.downloadObject(fileUploadInfo.filePath, response)
        }
    }

    private fun getUploadInfo(finalObjectsName: String, uploadId: String, part: Set<Int>): UploadInfo {
        val zSetTupleSet = part.map {
            val preSignedObjectUrl = minioHelper.preSignedObjectUrl(uploadId, finalObjectsName, it)
            ZSetTuple(preSignedObjectUrl, it.toDouble())
        }.toSet()
        stringRedisService.setZSet(uploadId, zSetTupleSet, minioConfigure.expires?.toLong() ?: 10, TimeUnit.SECONDS)
        return UploadInfo(uploadId = uploadId, part = part)
    }
}


private fun makeFilePath(filename: String?) =
    CollectionUtil.join(
        arrayListOf(
            LocalDate.now().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)),
            filename
        ), "/", null, null
    )

fun handleFileName(userAgent: String, fileName: String?): String? {
    return fileName?.let {
        var result = it
        result = if (userAgent.contains("MSIE") || userAgent.contains("TRIDENT") || userAgent.contains("EDGE")) {
            //IE下载文件名空格变+号问题
            URLEncoder.encode(it, StandardCharsets.UTF_8).replace("+", "%20")
        } else {
            String(it.toByteArray(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1)
        }
        return result
    }

}

data class FileOperationResult(
    var fileId: Long? = null,
    var fileStatus: FileStatus? = null,
    var chunkUploadedList: List<Int>? = null
)

data class FileUploadInfoDto(val id: Long, val fileName: String, val fileSize: Long)

data class MergeMultipartParam(val uploadId: String, val fileMd5: String)

data class UploadInfo(var uploadId: String? = null, var expiryTime: LocalDateTime? = null, var part: Set<Int>? = null)

data class UploadInfoParam(val fileName: String, val fileMd5: String, val fileSize: Double, val chunkSize: Double)


