package org.example.rushpurchase.service.impl

import org.example.rushpurchase.enums.FileStatus
import org.example.rushpurchase.mysql.entity.FileUploadInfo
import org.example.rushpurchase.mysql.repo.FileUploadInfoRepository
import org.example.rushpurchase.service.FileUploadInfoService
import org.springframework.stereotype.Service
import java.util.*

@Service
class FileUploadInfoServiceImpl(val fileUploadInfoRepository: FileUploadInfoRepository) : FileUploadInfoService {

    override fun saveFileUploadInfo(fileUploadInfo: FileUploadInfo): FileUploadInfo {
        return fileUploadInfoRepository.save(fileUploadInfo)
    }

    override fun saveFileFileStatus(fileMd5: String, fileStatus: FileStatus) {
        val fileUploadInfoOptional = fileUploadInfoRepository.findByFileMd5(fileMd5)
        fileUploadInfoOptional.ifPresent { fileUploadInfo: FileUploadInfo ->
            fileUploadInfo.fileStatus = fileStatus
            fileUploadInfoRepository.save(fileUploadInfo)
        }
    }

    override fun findFileUploadInfoById(id: Long): Optional<FileUploadInfo> {
        return fileUploadInfoRepository.findById(id)
    }

    override fun findFileUploadInfoByFileMd5(fileMd5: String): Optional<FileUploadInfo> {
        return fileUploadInfoRepository.findByFileMd5(fileMd5)
    }

    override fun findListFileUploadInfoById(ids: List<Long>): List<FileUploadInfo> {
        return fileUploadInfoRepository.findAllById(ids)
    }
}