package org.example.rushpurchase.service.impl

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.rushpurchase.cache.service.StringRedisService
import org.example.rushpurchase.enums.FileStatus
import org.example.rushpurchase.mysql.entity.FileUploadInfo
import org.example.rushpurchase.service.FileChunkUploadInfoService
import org.example.rushpurchase.service.FileService
import org.example.rushpurchase.service.FileUploadInfoService
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

class FileServiceImpl(
    fileUploadInfoService: FileUploadInfoService,
    fileChunkUploadInfoService: FileChunkUploadInfoService,
    stringRedisService: StringRedisService
) : FileService {
    override fun checkUploadFile(fileMd5: String): FileOperationResult {
        TODO("Not yet implemented")
    }

    override fun directUploadFile(multipartFile: MultipartFile): FileUploadInfo {
        TODO("Not yet implemented")
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

data class FileOperationResult(val fileId: Long, val fileStatus: FileStatus, val chunkUploadedList: List<Int>)

data class FileUploadInfoDto(val id: Long, val fileName: String, val fileSize: Long)

data class MergeMultipartParam(val uploadId: String, val fileMd5: String)

data class UploadInfo(val uploadId: String, val expiryTime: LocalDateTime, val part: Set<Int>)

data class UploadInfoParam(val fileName: String, val fileMd5: String, val fileSize: Double, val chunkSize: Double)


