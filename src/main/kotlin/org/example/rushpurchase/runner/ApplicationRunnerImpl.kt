package org.example.rushpurchase.runner

import io.github.oshai.kotlinlogging.KotlinLogging
import org.example.rushpurchase.minio.helper.MinioHelper
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class ApplicationRunnerImpl(val minioHelper: MinioHelper) : ApplicationRunner {
    private val log = KotlinLogging.logger {}
    override fun run(args: ApplicationArguments?) {
        minioHelper.createBuketName()
        log.info { "create buketName success" }
    }
}