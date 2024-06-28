package org.example.rushpurchase.mysql.repo;

import org.example.rushpurchase.mysql.entity.FileUploadInfo
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface FileUploadInfoRepository : JpaRepository<FileUploadInfo, Long> {


    fun findByFileMd5(fileMd5: String): Optional<FileUploadInfo>
}