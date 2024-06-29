package org.example.rushpurchase.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.io.Serializable

/**
 * DTO for {@link org.example.rushpurchase.mysql.entity.FileUploadInfo}
 */
data class FileUploadInfoDto(
    val id: Long? = null,
    @field:NotNull @field:Size(max = 255) val fileName: String? = null,
    val fileSize: Long? = null
) : Serializable