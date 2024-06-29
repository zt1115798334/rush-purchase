package org.example.rushpurchase.mysql.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.example.rushpurchase.enums.FileStatus
import org.hibernate.annotations.ColumnDefault
import java.time.Instant
import java.time.LocalDateTime

@Entity
@Table(name = "t_file_upload_info")
open class FileUploadInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Size(max = 255)
    @NotNull
    @Column(name = "file_name", nullable = false)
    open var fileName: String? = null

    @Size(max = 255)
    @NotNull
    @Column(name = "minio_file_name", nullable = false)
    open var minioFileName: String? = null

    @Size(max = 255)
    @NotNull
    @Column(name = "file_md5", nullable = false)
    open var fileMd5: String? = null

    @Size(max = 255)
    @NotNull
    @Column(name = "file_path", nullable = false)
    open var filePath: String? = null

    @NotNull
    @Lob
    @Column(name = "file_status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    open var fileStatus: FileStatus? = null

    @Column(name = "file_size")
    open var fileSize: Long? = null

    @Size(max = 255)
    @Column(name = "upload_id")
    open var uploadId: String? = null

    @NotNull
    @Column(name = "total_chunk", nullable = false)
    open var totalChunk: Int? = null

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_time", nullable = false)
    open var createdTime: LocalDateTime? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_time")
    open var updatedTime: LocalDateTime? = null
}