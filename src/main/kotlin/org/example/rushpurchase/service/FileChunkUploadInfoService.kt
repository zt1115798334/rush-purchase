package org.example.rushpurchase.service

import org.example.rushpurchase.mysql.entity.FileChunkUploadInfo

interface FileChunkUploadInfoService {
    fun saveFileChunkUploadInfo(uploadId: String, fileMd5: String, partCount: Int)

    fun findListByFileMd5AndUploadId(fileMd5: String, uploadId: String): List<FileChunkUploadInfo>

    fun updateChunkStatesToComplete(uploadId: String, chunkNumber: Int)
}