package org.example.rushpurchase.service

import org.example.rushpurchase.enums.FileStatus
import org.example.rushpurchase.mysql.entity.FileUploadInfo
import java.util.*

interface FileUploadInfoService {

    fun saveFileUploadInfo(fileUploadInfo: FileUploadInfo): FileUploadInfo

    fun saveFileFileStatus(fileMd5: String, fileStatus: FileStatus)

    fun findFileUploadInfoById(id: Long): Optional<FileUploadInfo>

    fun findFileUploadInfoByFileMd5(fileMd5: String): Optional<FileUploadInfo>

    fun findListFileUploadInfoById(ids: List<Long>): List<FileUploadInfo>
}