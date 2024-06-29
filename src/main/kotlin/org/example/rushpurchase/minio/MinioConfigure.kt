package org.example.rushpurchase.minio

data class MinioConfigure(
    var prefix: String = MinioConstant.PREFIX,
    var endpoint: String? = null,
    var accessKey: String? = null,
    var secretKey: String? = null,
    var bucketName: String? = null,
    var expires: Int? = null

)

class MinioConstant {
    companion object {
        const val PREFIX = "org.zt.minio"
    }
}

