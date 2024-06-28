package org.example.rushpurchase.minio.helper

import cn.hutool.core.io.IoUtil
import cn.hutool.http.ContentType
import com.google.common.collect.HashMultimap
import io.github.oshai.kotlinlogging.KotlinLogging
import io.minio.GetObjectResponse
import io.minio.ListPartsResponse
import io.minio.ObjectWriteResponse
import io.minio.StatObjectResponse
import io.minio.errors.InsufficientDataException
import io.minio.errors.InternalException
import io.minio.errors.XmlParserException
import io.minio.messages.Part
import jakarta.servlet.http.HttpServletResponse
import org.example.rushpurchase.minio.MinioConfigure
import org.example.rushpurchase.minio.template.MinioTemplate
import org.springframework.stereotype.Component
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors

@Component
class MinioHelper(val minioTemplate: MinioTemplate,val minioConfigure: MinioConfigure) {
    private val log = KotlinLogging.logger {}
    fun createBuketName() {
        try {
            val bool: Boolean = minioTemplate.bucketExists(minioConfigure.bucketName).join()
            if (bool.not()) {
                minioTemplate.createBucket(minioConfigure.bucketName)
            }
        } catch (e: IOException) {
            log.error { "createBuketName失败：$e" }
            throw RuntimeException("createBuketName失败：" + e.message)
        } catch (e: InvalidKeyException) {
            log.error("createBuketName失败：$e")
            throw RuntimeException("createBuketName失败：" + e.message)
        } catch (e: NoSuchAlgorithmException) {
            log.error("createBuketName失败：$e")
            throw RuntimeException("createBuketName失败：" + e.message)
        } catch (e: XmlParserException) {
            log.error("createBuketName失败：$e")
            throw RuntimeException("createBuketName失败：" + e.message)
        } catch (e: InsufficientDataException) {
            log.error("createBuketName失败：$e")
            throw RuntimeException("createBuketName失败：" + e.message)
        } catch (e: InternalException) {
            log.error("createBuketName失败：$e")
            throw RuntimeException("createBuketName失败：" + e.message)
        }
    }

    /**
     * 获取外链
     *
     * @param objectName 文件名
     * @return String
     */
    fun getObjectUrlNotExpire(objectName: String?): String {
        val result: String
        try {
            result = minioTemplate.getObjectUrl(minioConfigure.bucketName, objectName)
        } catch (e: Exception) {
            log.error("获取外链失败：$e")
            throw RuntimeException("获取外链失败：" + e.message)
        }
        return result
    }

    /**
     * 获取外链
     *
     * @param objectName 文件名
     * @return String
     */
    fun getObjectUrl(objectName: String?): String {
        val result: String
        try {
            result = minioTemplate.getObjectUrl(minioConfigure.bucketName, objectName, minioConfigure.expires)
        } catch (e: Exception) {
            log.error("获取外链失败：$e")
            throw RuntimeException("获取外链失败：" + e.message)
        }
        return result
    }


    /**
     * 获取文件
     *
     * @param objectName 文件名
     */
    @Throws(
        IOException::class,
        InsufficientDataException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        XmlParserException::class,
        InternalException::class
    )
    fun downloadObject(objectName: String?): CompletableFuture<GetObjectResponse> {
        return minioTemplate.getObject(minioConfigure.bucketName, objectName)
    }

    /**
     * 下载文件到本地
     *
     * @param objectName 文件名
     * @param targetPath 保存地址
     */
    @Throws(IOException::class)
    fun downloadObject(objectName: String?, targetPath: Path): String {
        if (Files.notExists(targetPath.parent)) {
            Files.createDirectories(targetPath.parent)
        }
        try {
            minioTemplate.getObject(minioConfigure.bucketName, objectName).get().use { response ->
                val buf = ByteArray(1024)
                var len: Int
                FileOutputStream(targetPath.toString()).use { os ->
                    while ((response.read(buf).also { len = it }) != -1) {
                        os.write(buf, 0, len)
                    }
                    os.flush()
                }
            }
        } catch (e: Exception) {
            log.error("获取外链失败：$e")
            throw RuntimeException("获取外链失败：" + e.message)
        }
        return targetPath.toString()
    }

    /**
     * 下载文件到本地
     *
     * @param objectName 文件名
     * @param res        res
     */
    @Throws(IOException::class)
    fun downloadObject(objectName: String?, res: HttpServletResponse) {
        try {
            minioTemplate.getObject(minioConfigure.bucketName, objectName).join().use { response ->
                IoUtil.copy(response, res.outputStream)
            }
        } catch (e: Exception) {
            log.error("获取外链失败：" + e.message)
            throw RuntimeException("获取外链失败：" + e.message)
        }
    }

    /**
     * 初始化获取 uploadId
     *
     * @param objectName 文件名
     * @return UploadInfo
     */
    fun initMultiPartUpload(objectName: String?): String? {
        val headers = HashMultimap.create<String?, String?>()
        headers.put("Content-Type", ContentType.OCTET_STREAM.value)
        val uploadId: String
        try {
            // 获取 uploadId
            uploadId = minioTemplate.initMultiPartUpload(
                minioConfigure.bucketName,
                null,
                objectName,
                headers,
                null
            )
        } catch (e: Exception) {
            log.error("initMultiPartUpload Error:$e")
            return null
        }
        return uploadId
    }

    fun preSignedObjectUrl(uploadId: String?, objectName: String?, part: Int): String {
        val uploadUrl: String
        try {
            val paramsMap: MutableMap<String?, String?> = HashMap(2)
            paramsMap["uploadId"] = uploadId
            paramsMap["partNumber"] = part.toString()
            uploadUrl = minioTemplate.preSignedObjectUrl(
                minioConfigure.bucketName, objectName,
                minioConfigure.expires, paramsMap
            )
        } catch (e: Exception) {
            log.error("preSignedObjectUrl Error:$e")
            throw RuntimeException("获取外链失败：" + e.message)
        }
        return uploadUrl
    }

    fun preSignedObjectUrl(uploadId: String?, objectName: String?, partArr: List<Int>): List<String>? {
        val partUrlList: MutableList<String> = ArrayList()
        try {
            val paramsMap: MutableMap<String?, String?> = HashMap(2)
            paramsMap["uploadId"] = uploadId
            for (i in partArr) {
                paramsMap["partNumber"] = i.toString()
                val uploadUrl: String = minioTemplate.preSignedObjectUrl(
                    minioConfigure.bucketName, objectName,
                    minioConfigure.expires, paramsMap
                )
                partUrlList.add(uploadUrl)
            }
        } catch (e: Exception) {
            log.error { "preSignedObjectUrl Error:$e" }
            return null
        }
        return partUrlList
    }

    /**
     * 分片合并
     *
     * @param objectName 文件名
     * @param uploadId   uploadId
     * @return String
     */
    fun mergeMultiPartUpload(objectName: String, uploadId: String): String {
        val parts = arrayOfNulls<Part>(1000)
        var partIndex = 0
        val partsResponse = listUploadPartsBase(objectName, uploadId)
        if (null == partsResponse) {
            log.error { "查询文件分片列表为空" }
            throw RuntimeException("分片列表为空")
        }
        for (partItem in partsResponse.result().partList()) {
            parts[partIndex] = Part(partIndex + 1, partItem.etag())
            partIndex++
        }
        val objectWriteResponse: ObjectWriteResponse
        try {
            objectWriteResponse = minioTemplate.mergeMultipartUpload(
                minioConfigure.bucketName,
                null,
                objectName,
                uploadId,
                parts,
                null,
                null
            )
        } catch (e: Exception) {
            log.error("分片合并失败：$e")
            throw RuntimeException("分片合并失败：" + e.message)
        }
        if (null == objectWriteResponse) {
            log.error("合并失败，合并结果为空")
            throw RuntimeException("分片合并失败")
        }
        return objectWriteResponse.region()
    }


    @Throws(
        InsufficientDataException::class,
        IOException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        XmlParserException::class,
        InternalException::class
    )
    fun uploadObject(objectName: String?, fileName: String?): CompletableFuture<ObjectWriteResponse> {
        return minioTemplate.uploadObject(
            minioConfigure.bucketName,
            objectName,
            fileName,
            ContentType.OCTET_STREAM.value
        )
    }

    @Throws(Exception::class)
    fun putObject(objectName: String?, inputStream: InputStream?): CompletableFuture<ObjectWriteResponse> {
        return minioTemplate.putObject(minioConfigure.bucketName, objectName, inputStream!!)
    }


    /**
     * 获取已上传的分片列表
     *
     * @param objectName 文件名
     * @param uploadId   uploadId
     * @return List<Integer>
    </Integer> */
    fun listUploadChunkList(objectName: String, uploadId: String): List<Int> {
        val partsResponse = listUploadPartsBase(objectName, uploadId) ?: return emptyList()
        return partsResponse.result().partList().stream().map { obj: Part -> obj.partNumber() }
            .collect(Collectors.toList())
    }

    private fun listUploadPartsBase(objectName: String, uploadId: String): ListPartsResponse? {
        val maxParts = 1000
        val partsResponse: ListPartsResponse
        try {
            partsResponse = minioTemplate.listMultipart(
                minioConfigure.bucketName,
                null,
                objectName,
                maxParts,
                0,
                uploadId,
                null,
                null
            )
        } catch (e: Exception) {
            log.error("查询文件分片列表错误：{}，uploadId:{}", e, uploadId)
            return null
        }
        return partsResponse
    }

    fun statObject(objectName: String?): StatObjectResponse? {
        try {
            return minioTemplate.statObject(minioConfigure.bucketName, null, objectName).get()
        } catch (e: Exception) {
            log.error("获取StatObjectResponse报错：{}，objectName:{}", e, objectName)
            return null
        }
    }
}