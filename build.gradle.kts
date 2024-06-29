plugins {
    alias(libs.plugins.spring.boot.library)
    alias(libs.plugins.dependency.management.library)
    alias(libs.plugins.kotlin.jvm.library)
    alias(libs.plugins.kotlin.kapt.library)
    alias(libs.plugins.kotlin.plugin.spring.library)

}

group = "org.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(libs.springdoc.openapi.starter.webmvc.ui.library)
    implementation(libs.springdoc.openapi.starter.webmvc.api.library)


    implementation(libs.minio.library)
    {
        exclude("com.google.guava", "guava")
    }
    implementation(libs.hutool.all.library)
    implementation(libs.fastjson.library)
    implementation(libs.guava.library)
    implementation(libs.mapstruct.library)
    kapt(libs.mapstruct.processor.library)
    kaptTest(libs.mapstruct.processor.library)
    implementation(libs.commons.compress.library)
    implementation(libs.kotlin.logging.library)
    implementation(libs.kotlinx.datetime.library)
    implementation(libs.kotlinx.serialization.json.library)
    implementation(libs.querydsl.core.library)
    implementation(
        variantOf(libs.querydsl.jpa.library) { classifier("jakarta") }
    )
    implementation(
        variantOf(libs.shiro.core.library) { classifier("jakarta") }
    )
    implementation(
        variantOf(libs.shiro.web.library) { classifier("jakarta") }
    )
    implementation(
        variantOf(libs.shiro.spring.library) { classifier("jakarta") }
    ) {
        exclude("org.apache.shiro", "shiro-core")
        exclude("org.apache.shiro", "shiro-web")
    }
    implementation(libs.shiro.redis.library)
    {
        exclude("com.google.guava", "guava")
    }
    kapt(
        variantOf(libs.querydsl.apt.library) { classifier("jakarta") },
    )

    kapt(libs.jakarta.persistence.api.library)

    implementation(libs.blaze.persistence.core.api.jakarta.library)
    implementation(libs.blaze.persistence.core.impl.jakarta.library)
    implementation(libs.blaze.persistence.integration.hibernate.library)
    implementation(libs.blaze.persistence.integration.querydsl.expressions.jakarta.library)

    runtimeOnly("com.mysql:mysql-connector-j")

}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kapt {
    arguments {
        // Set Mapstruct Configuration options here
        // https://kotlinlang.org/docs/reference/kapt.html#annotation-processor-arguments
        // https://mapstruct.org/documentation/stable/reference/html/#configuration-options
        // 注入spring 容器
        arg("mapstruct.defaultComponentModel", "spring")
    }
}
