package org.example.rushpurchase.mysql.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.example.rushpurchase.enums.ChunkStatus
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime

@Entity
@Table(name = "t_file_chunk_upload_info")
open class FileChunkUploadInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Size(max = 255)
    @NotNull
    @Column(name = "file_md5", nullable = false)
    open var fileMd5: String? = null

    @NotNull
    @Column(name = "chunk_number", nullable = false)
    open var chunkNumber: Int? = null

    @Size(max = 255)
    @NotNull
    @Column(name = "upload_id", nullable = false)
    open var uploadId: String? = null

    @NotNull
    @ColumnDefault("PENDING")
    @Lob
    @Enumerated(value = EnumType.STRING)
    @Column(name = "chunk_status", nullable = false)
    open var chunkStatus: ChunkStatus? = null

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_time", nullable = false)
    open var createdTime: LocalDateTime? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_time")
    open var updatedTime: LocalDateTime? = null
}