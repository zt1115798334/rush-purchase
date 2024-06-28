package org.example.rushpurchase.minio

data class MinioConfigure(
    val prefix: String = MinioConstant.PREFIX,
    val endpoint: String? = null,
    val accessKey: String? = null,
    val secretKey: String? = null,
    val bucketName: String? = null,
    val expires: Int? = null

)

class MinioConstant {
    companion object {
        const val PREFIX = "org.zt.minio"
    }
}

