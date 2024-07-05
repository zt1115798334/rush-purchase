package org.example.rushpurchase.controller

import com.alibaba.fastjson2.annotation.JSONField
import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.rushpurchase.base.BaseResultMessage
import org.example.rushpurchase.base.ResultMessage
import org.example.rushpurchase.dto.mapper.FileUploadInfoMapper
import org.example.rushpurchase.service.FileService
import org.example.rushpurchase.service.impl.MergeMultipartParam
import org.example.rushpurchase.service.impl.UploadInfoParam
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(value = ["/api/file"])
class FileController(val fileService: FileService, val fileUploadInfoMapper: FileUploadInfoMapper) :
    BaseResultMessage() {

    @Operation(summary = "检查文件是否上传")
    @GetMapping(value = ["/checkUploadFile"])
    fun checkUploadFile(@RequestParam fileMd5: String?): ResultMessage {
        val result = fileService.checkUploadFile(fileMd5!!)
        return success(result)
    }

    @Operation(summary = "直接上传文件")
    @PostMapping(
        value = ["/directUploadFile"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun directUploadFile(@RequestParam("file") @JSONField(serialize = false) multipartFile: MultipartFile?): ResultMessage {
        val fileUploadInfo = fileService.directUploadFile(multipartFile!!)
        return success(fileUploadInfoMapper.toDto(fileUploadInfo))
    }

    @Operation(summary = "上传文件")
    @PostMapping(
        value = ["/uploadFile"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun uploadFile(@Validated @RequestBody param: UploadInfoParam): ResultMessage {
        val minioUploadId = fileService.uploadFile(param)
        return minioUploadId?.let { success(it) } ?: failure("上传文件错误")
    }

    @Operation(summary = "下载文件")
    @PostMapping(
        value = ["/downloadFile"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    fun downloadFile(request: HttpServletRequest, response: HttpServletResponse, @RequestParam fileId: Long) {
        fileService.downloadFile(fileId, request, response)
    }

    @Operation(summary = "上传文件块")
    @PostMapping(
        value = ["/uploadFileChunk"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun uploadFileChunk(
        @RequestParam("file") @JSONField(serialize = false) multipartFile: MultipartFile,
        @RequestParam uploadId: String,
        @RequestParam chunkNumber: Int
    ): ResultMessage {
        fileService.uploadFileChunk(multipartFile, uploadId, chunkNumber)
        return success()
    }

    @Operation(summary = "合并文件")
    @PostMapping(value = ["/mergeUploadFile"])
    fun mergeUploadFile(@RequestBody param: MergeMultipartParam): ResultMessage {
        val result = fileService.mergeMultipartUpload(param)
        return success(result)
    }

    @Operation(summary = "显示图片文件")
    @GetMapping(value = ["/showFile"])
    fun showFile(request: HttpServletRequest, response: HttpServletResponse, @RequestParam fileId: Long) {
        fileService.showFile(fileId, request, response)
    }
}