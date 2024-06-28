package org.example.rushpurchase.minio.client

import com.google.common.collect.Multimap
import io.minio.ListPartsResponse
import io.minio.MinioAsyncClient
import io.minio.ObjectWriteResponse
import io.minio.errors.InsufficientDataException
import io.minio.errors.InternalException
import io.minio.errors.XmlParserException
import io.minio.messages.Part
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException

class CustomMinioClient(client: MinioAsyncClient) : MinioAsyncClient(client) {
    @Throws(
        IOException::class,
        NoSuchAlgorithmException::class,
        InsufficientDataException::class,
        InternalException::class,
        XmlParserException::class,
        InvalidKeyException::class
    )
    fun initMultiPartUpload(
        bucketName: String?,
        region: String?,
        `object`: String?,
        headers: Multimap<String?, String?>?,
        extraQueryParams: Multimap<String?, String?>?
    ): String {
        val response = createMultipartUploadAsync(bucketName, region, `object`, headers, extraQueryParams).join()
        return response.result().uploadId()
    }

    @Throws(
        IOException::class,
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        InsufficientDataException::class,
        InternalException::class,
        XmlParserException::class
    )
    fun mergeMultipartUpload(
        bucketName: String?,
        region: String?,
        objectName: String?,
        uploadId: String?,
        parts: Array<Part?>?,
        extraHeaders: Multimap<String?, String?>?,
        extraQueryParams: Multimap<String?, String?>?
    ): ObjectWriteResponse {
        return completeMultipartUploadAsync(
            bucketName,
            region,
            objectName,
            uploadId,
            parts,
            extraHeaders,
            extraQueryParams
        ).join()
    }

    @Throws(
        NoSuchAlgorithmException::class,
        InsufficientDataException::class,
        IOException::class,
        InvalidKeyException::class,
        XmlParserException::class,
        InternalException::class
    )
    fun listMultipart(
        bucketName: String?,
        region: String?,
        objectName: String?,
        maxParts: Int?,
        partNumberMarker: Int?,
        uploadId: String?,
        extraHeaders: Multimap<String?, String?>?,
        extraQueryParams: Multimap<String?, String?>?
    ): ListPartsResponse {
        return listPartsAsync(
            bucketName,
            region,
            objectName,
            maxParts,
            partNumberMarker,
            uploadId,
            extraHeaders,
            extraQueryParams
        ).join()
    }
}