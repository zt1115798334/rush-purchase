package org.example.rushpurchase.minio

import io.minio.MinioAsyncClient
import org.example.rushpurchase.minio.client.CustomMinioClient
import org.example.rushpurchase.minio.template.MinioTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.*
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(MinioAsyncClient::class)
class NauMinioAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = MinioConstant.PREFIX)
    @ConditionalOnProperty(prefix = MinioConstant.PREFIX, value = ["endpoint"])
    fun minioConfigure(): MinioConfigure {
        return MinioConfigure()
    }

    @Bean
    @ConditionalOnBean(MinioConfigure::class)
    @ConditionalOnSingleCandidate(
        MinioConfigure::class
    )
    @ConditionalOnMissingBean(MinioAsyncClient::class)
    fun minioClient(@Autowired minioConfigure: MinioConfigure): MinioAsyncClient {
        return MinioAsyncClient.builder()
            .endpoint(minioConfigure.endpoint)
            .credentials(minioConfigure.accessKey, minioConfigure.secretKey)
            .build()
    }


    @Bean
    @ConditionalOnBean(MinioAsyncClient::class)
    @ConditionalOnSingleCandidate(
        MinioAsyncClient::class
    )
    @ConditionalOnMissingBean(CustomMinioClient::class)
    fun customMinioClient(@Autowired minioClient: MinioAsyncClient): CustomMinioClient {
        return CustomMinioClient(minioClient)
    }

    @Bean
    @ConditionalOnBean(CustomMinioClient::class)
    @ConditionalOnSingleCandidate(
        CustomMinioClient::class
    )
    @ConditionalOnMissingBean(MinioTemplate::class)
    fun minioTemplate(@Autowired customMinioClient: CustomMinioClient?): MinioTemplate {
        return MinioTemplate(customMinioClient)
    }

}