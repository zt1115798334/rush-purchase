package org.example.rushpurchase.service

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.rushpurchase.mysql.entity.FileUploadInfo
import org.example.rushpurchase.service.impl.FileOperationResult
import org.example.rushpurchase.service.impl.MergeMultipartParam
import org.example.rushpurchase.service.impl.UploadInfo
import org.example.rushpurchase.service.impl.UploadInfoParam
import org.springframework.web.multipart.MultipartFile

interface FileService {

    fun checkUploadFile(fileMd5:String): FileOperationResult

    fun directUploadFile(multipartFile: MultipartFile):FileUploadInfo

    fun uploadFile(uploadInfoParam: UploadInfoParam):UploadInfo?

    fun uploadFileChunk(multipartFile: MultipartFile, uploadId: String, chunkNumber: Int)

    fun mergeMultipartUpload(param: MergeMultipartParam):FileOperationResult

    fun downloadFile(fileId: Long, request: HttpServletRequest, response: HttpServletResponse)

    fun showFile(fileId: Long, request: HttpServletRequest, response: HttpServletResponse)



}