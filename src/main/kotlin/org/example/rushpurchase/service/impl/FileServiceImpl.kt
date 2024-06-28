package org.example.rushpurchase.service.impl

import cn.hutool.core.collection.CollectionUtil
import cn.hutool.core.date.DatePattern
import cn.hutool.core.date.DateUtil
import cn.hutool.core.io.file.FileNameUtil
import cn.hutool.core.util.IdUtil
import cn.hutool.crypto.digest.MD5
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.rushpurchase.cache.service.StringRedisService
import org.example.rushpurchase.enums.FileStatus
import org.example.rushpurchase.minio.MinioConfigure
import org.example.rushpurchase.minio.helper.MinioHelper
import org.example.rushpurchase.mysql.entity.FileUploadInfo
import org.example.rushpurchase.service.FileChunkUploadInfoService
import org.example.rushpurchase.service.FileService
import org.example.rushpurchase.service.FileUploadInfoService
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
                    log.info { "${"objectName：{}上传成功，耗时：{}"} $minioFileName ${timer.intervalPretty()}" }
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

    override fun uploadFile(uploadInfoParam: UploadInfoParam): UploadInfo {
        TODO("Not yet implemented")
    }

    override fun uploadFileChunk(multipartFile: MultipartFile, uploadId: String, chunkNumber: Int) {
        TODO("Not yet implemented")
    }

    override fun mergeMultipartUpload(param: MergeMultipartParam) {
        TODO("Not yet implemented")
    }

    override fun downloadFile(fileId: Long, request: HttpServletRequest, response: HttpServletResponse) {
        TODO("Not yet implemented")
    }

    override fun showFile(fileId: Long, request: HttpServletRequest, response: HttpServletResponse) {
        TODO("Not yet implemented")
    }

}


fun makeFilePath(filename: String?) =
    CollectionUtil.join(
        arrayListOf(
            LocalDate.now().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)),
            filename
        ), "/", null, null
    )


data class FileOperationResult(
    var fileId: Long? = null,
    var fileStatus: FileStatus? = null,
    var chunkUploadedList: List<Int>? = null
)

data class FileUploadInfoDto(val id: Long, val fileName: String, val fileSize: Long)

data class MergeMultipartParam(val uploadId: String, val fileMd5: String)

data class UploadInfo(val uploadId: String, val expiryTime: LocalDateTime, val part: Set<Int>)

data class UploadInfoParam(val fileName: String, val fileMd5: String, val fileSize: Double, val chunkSize: Double)


