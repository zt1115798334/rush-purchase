package org.example.rushpurchase.service.impl

import org.example.rushpurchase.enums.ChunkStatus
import org.example.rushpurchase.mysql.entity.FileChunkUploadInfo
import org.example.rushpurchase.mysql.repo.FileChunkUploadInfoRepository
import org.example.rushpurchase.service.FileChunkUploadInfoService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class FileChunkUploadInfoServiceImpl(val fileChunkUploadInfoRepository: FileChunkUploadInfoRepository) :
    FileChunkUploadInfoService {

    override fun saveFileChunkUploadInfo(uploadId: String, fileMd5: String, partCount: Int) {
        val chunkUploadInfoList = List(10) { index ->
            val f = FileChunkUploadInfo()
            f.chunkNumber = index
            f.fileMd5 = fileMd5
            f.uploadId = uploadId
            f.chunkStatus = ChunkStatus.PENDING
            f.createdTime = LocalDateTime.now()
            f
        }
        fileChunkUploadInfoRepository.saveAll(chunkUploadInfoList)

    }

    override fun findListByFileMd5AndUploadId(fileMd5: String, uploadId: String): List<FileChunkUploadInfo> {
        return fileChunkUploadInfoRepository.findByFileMd5AndUploadId(fileMd5, uploadId)
    }

    override fun updateChunkStatesToComplete(uploadId: String, chunkNumber: Int) {
        fileChunkUploadInfoRepository.updateChunkStatusByUploadIdAndChunkNumber(ChunkStatus.COMPLETE, uploadId, chunkNumber)
    }
}