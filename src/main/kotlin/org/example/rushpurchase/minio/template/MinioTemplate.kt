package org.example.rushpurchase.minio.template

import com.google.common.collect.Multimap
import io.minio.*
import io.minio.errors.InsufficientDataException
import io.minio.errors.InternalException
import io.minio.errors.XmlParserException
import io.minio.http.Method
import io.minio.messages.Bucket
import io.minio.messages.Item
import io.minio.messages.Part
import org.example.rushpurchase.minio.client.CustomMinioClient
import org.springframework.stereotype.Component
import java.io.IOException
import java.io.InputStream
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.CompletableFuture

@Component

class MinioTemplate(private val customMinioClient: CustomMinioClient) {
    /**
     * 检查文件存储桶是否存在
     *
     * @param bucketName bucket name
     * @return boolean True if the bucket exists.
     */
    @Throws(
        IOException::class,
        InvalidKeyException::class,
        InsufficientDataException::class,
        NoSuchAlgorithmException::class,
        InternalException::class,
        XmlParserException::class
    )
    fun bucketExists(bucketName: String?): CompletableFuture<Boolean> {
        return customMinioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())
    }

    /**
     * 创建bucket
     *
     * @param bucketName bucket名称
     */
    @Throws(
        IOException::class,
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        XmlParserException::class,
        InsufficientDataException::class,
        InternalException::class
    )
    fun createBucket(bucketName: String?): CompletableFuture<Void> {
        return customMinioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
    }

    /**
     * 获取全部bucket
     *
     *
     */
    @Throws(
        IOException::class,
        InvalidKeyException::class,
        InsufficientDataException::class,
        NoSuchAlgorithmException::class,
        InternalException::class,
        XmlParserException::class
    )
    fun getAllBuckets(): CompletableFuture<List<Bucket>> {
        return customMinioClient.listBuckets()
    }

    /**
     * 根据bucketName获取信息
     *
     * @param bucketName bucket名称
     */
    @Throws(
        IOException::class,
        InvalidKeyException::class,
        InsufficientDataException::class,
        NoSuchAlgorithmException::class,
        InternalException::class,
        XmlParserException::class
    )
    fun getBucket(bucketName: String?): Optional<Bucket> {
        return customMinioClient.listBuckets().join().stream().filter { b -> b.name().equals(bucketName) }.findFirst()
    }

    /**
     * 根据bucketName删除信息
     *
     * @param bucketName bucket名称
     */
    @Throws(
        IOException::class,
        InvalidKeyException::class,
        InsufficientDataException::class,
        NoSuchAlgorithmException::class,
        InternalException::class,
        XmlParserException::class
    )
    fun removeBucket(bucketName: String?): CompletableFuture<Void> {
        return customMinioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build())
    }


    /**
     * 列出桶的对象信息。
     *
     * @param args ListObjectsArgs
     * @return Iterable<Result></Result> < Item>>
     */
    fun listObjects(args: ListObjectsArgs?): Iterable<Result<Item>> {
        return customMinioClient.listObjects(args)
    }

    /**
     * 列出指定前缀的桶对象信息
     *
     * @param bucketName bucket name
     * @param prefix     前缀
     * @param recursive  是否递归
     * @return Iterable<Result></Result> < Item>>
     */
    fun getAllObjectsByPrefix(bucketName: String?, prefix: String?, recursive: Boolean): Iterable<Result<Item>> {
        return customMinioClient.listObjects(
            ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(recursive).build()
        )
    }

    /**
     * 列出对象信息，以prefix开头，在startAfter之后。
     *
     * @param bucketName bucket name
     * @param prefix     前缀
     * @param startAfter start after
     * @return Iterable<Result></Result> < Item>>
     */
    fun getAllObjectsByPrefixAndStartAfter(
        bucketName: String?,
        prefix: String?,
        startAfter: String?
    ): Iterable<Result<Item>> {
        return customMinioClient.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucketName).startAfter(startAfter).prefix(prefix).includeVersions(true).build()
        )
    }


    /**
     * 获取文件外链 没有过期时间
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return url
     */
    @Throws(Exception::class)
    fun getObjectUrl(bucketName: String?, objectName: String?): String {
        return customMinioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucketName).`object`(objectName).build()
        )
    }

    /**
     * 获取文件外链
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param expires    过期时间 秒
     * @return url
     */
    @Throws(Exception::class)
    fun getObjectUrl(bucketName: String?, objectName: String?, expires: Int?): String {
        return customMinioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucketName).`object`(objectName).expiry(
                expires!!
            ).build()
        )
    }

    /**
     * 获取文件二进制流
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return 二进制流
     */
    @Throws(
        IOException::class,
        InvalidKeyException::class,
        InsufficientDataException::class,
        NoSuchAlgorithmException::class,
        InternalException::class,
        XmlParserException::class
    )
    fun getObject(bucketName: String?, objectName: String?): CompletableFuture<GetObjectResponse> {
        return customMinioClient.getObject(GetObjectArgs.builder().bucket(bucketName).`object`(objectName).build())
    }

    /**
     * 下载文件
     *
     * @param bucketName bucket name
     * @param objectName object name
     * @param filename   输出文件名
     */
    @Throws(Exception::class)
    fun downloadObject(bucketName: String?, objectName: String?, filename: String?): CompletableFuture<Void> {
        return customMinioClient.downloadObject(
            DownloadObjectArgs.builder().bucket(bucketName).`object`(objectName).filename(filename).build()
        )
    }

    /**
     * 上传文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param stream     文件流
     */
    @Throws(Exception::class)
    fun putObject(
        bucketName: String?,
        objectName: String?,
        stream: InputStream
    ): CompletableFuture<ObjectWriteResponse> {
        return customMinioClient.putObject(
            PutObjectArgs.builder().bucket(bucketName).`object`(objectName)
                .stream(stream, stream.available().toLong(), -1)
                .contentType("application/octet-stream").build()
        )
    }

    /**
     * 上传文件
     *
     * @param bucketName  bucket名称
     * @param objectName  文件名称
     * @param stream      文件流
     * @param size        大小
     * @param contextType 类型
     */
    @Throws(Exception::class)
    fun putObject(
        bucketName: String?,
        objectName: String?,
        stream: InputStream,
        size: Long,
        contextType: String?
    ): CompletableFuture<ObjectWriteResponse> {
        return customMinioClient.putObject(
            PutObjectArgs.builder().bucket(bucketName).`object`(objectName)
                .stream(stream, stream.available().toLong(), -1).contentType(contextType).build()
        )
    }

    @Throws(
        IOException::class,
        InsufficientDataException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        XmlParserException::class,
        InternalException::class
    )
    fun uploadObject(
        bucketName: String?,
        objectName: String?,
        fileName: String?,
        contentType: String?
    ): CompletableFuture<ObjectWriteResponse> {
        return customMinioClient.uploadObject(
            UploadObjectArgs
                .builder()
                .bucket(bucketName)
                .`object`(objectName)
                .filename(fileName)
                .contentType(contentType)
                .build()
        )
    }

    /**
     * 获取文件信息和元数据
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     */
    @Throws(Exception::class)
    fun getObjectInfo(bucketName: String?, objectName: String?): CompletableFuture<StatObjectResponse> {
        return customMinioClient.statObject(StatObjectArgs.builder().bucket(bucketName).`object`(objectName).build())
    }

    /**
     * 获取文件信息和元数据
     *
     * @param args args
     * @return StatObjectResponse
     * @throws Exception 异常
     */
    @Throws(Exception::class)
    fun getObjectInfo(args: StatObjectArgs?): CompletableFuture<StatObjectResponse> {
        return customMinioClient.statObject(args)
    }

    /**
     * 删除文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     */
    @Throws(Exception::class)
    fun removeObject(bucketName: String?, objectName: String?): CompletableFuture<Void> {
        return customMinioClient.removeObject(
            RemoveObjectArgs.builder().bucket(bucketName).`object`(objectName).build()
        )
    }

    /**
     * 获取上传URL
     *
     * @param bucketName bucket name
     * @param objectName object name
     * @param expires    过期时间 秒
     * @return String 上传URL
     * @throws Exception 异常
     */
    @Throws(Exception::class)
    fun preSignedObjectUrl(
        bucketName: String?,
        objectName: String?,
        expires: Int?,
        extraQueryParams: Map<String?, String?>
    ): String {
        return customMinioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.PUT)
                .bucket(bucketName)
                .`object`(objectName)
                .expiry(expires!!)
                .extraQueryParams(extraQueryParams)
                .build()
        )
    }

    /**
     * 初始化
     *
     * @param bucketName       Name of the bucket.
     * @param region           Region name of buckets in S3 service.
     * @param objectName       Object name in the bucket.
     * @param headers          Request headers.
     * @param extraQueryParams Extra query parameters for request (Optional).
     * @return String 上传URL
     * @throws Exception 异常
     */
    @Throws(Exception::class)
    fun initMultiPartUpload(
        bucketName: String?,
        region: String?,
        objectName: String?,
        headers: Multimap<String?, String?>?,
        extraQueryParams: Multimap<String?, String?>?
    ): String {
        return customMinioClient.initMultiPartUpload(bucketName, region, objectName, headers, extraQueryParams)
    }

    /**
     * 合并
     *
     * @param bucketName       Name of the bucket.
     * @param region           Region of the bucket.
     * @param objectName       Object name in the bucket.
     * @param uploadId         Upload ID.
     * @param parts            List of parts.
     * @param extraHeaders     Extra headers (Optional).
     * @param extraQueryParams Extra query parameters (Optional).
     * @return String 上传URL
     * @throws Exception 异常
     */
    @Throws(Exception::class)
    fun mergeMultipartUpload(
        bucketName: String?,
        region: String?,
        objectName: String?,
        uploadId: String?,
        parts: Array<Part?>?,
        extraHeaders: Multimap<String?, String?>?,
        extraQueryParams: Multimap<String?, String?>?
    ): ObjectWriteResponse {
        return customMinioClient.mergeMultipartUpload(
            bucketName,
            region,
            objectName,
            uploadId,
            parts,
            extraHeaders,
            extraQueryParams
        )
    }

    /**
     * 遍历
     *
     * @param bucketName       Name of the bucket.
     * @param region           Name of the bucket (Optional).
     * @param objectName       Object name in the bucket.
     * @param maxParts         Maximum parts information to fetch (Optional).
     * @param partNumberMarker Part number marker (Optional).
     * @param uploadId         Upload ID.
     * @param extraHeaders     Extra headers for request (Optional).
     * @param extraQueryParams Extra query parameters for request (Optional).
     * @return String 上传URL
     * @throws Exception 异常
     */
    @Throws(Exception::class)
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
        return customMinioClient.listMultipart(
            bucketName,
            region,
            objectName,
            maxParts,
            partNumberMarker,
            uploadId,
            extraHeaders,
            extraQueryParams
        )
    }

    @Throws(
        InsufficientDataException::class,
        IOException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        XmlParserException::class,
        InternalException::class
    )
    fun statObject(bucketName: String?, region: String?, objectName: String?): CompletableFuture<StatObjectResponse> {
        return customMinioClient.statObject(
            StatObjectArgs.builder().bucket(bucketName).region(region).`object`(objectName).build()
        )
    }
}