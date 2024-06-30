package org.example.rushpurchase.mysql.repo

import org.example.rushpurchase.enums.ChunkStatus
import org.example.rushpurchase.mysql.entity.FileChunkUploadInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface FileChunkUploadInfoRepository : JpaRepository<FileChunkUploadInfo, Long> {


    fun findByFileMd5AndUploadId(fileMd5: String, uploadId: String): List<FileChunkUploadInfo>


    @Transactional
    @Modifying
    @Query("update FileChunkUploadInfo f set f.chunkStatus = ?1 where f.uploadId = ?2 and f.chunkNumber = ?3")
    fun updateChunkStatusByUploadIdAndChunkNumber(chunkStatus: ChunkStatus, uploadId: String, chunkNumber: Int): Int
}