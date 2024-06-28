package org.example.rushpurchase.config

import cn.hutool.core.date.DatePattern
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Configuration
class JacksonConfig {
    @Bean
    fun objectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper {
        val normDatetimePattern = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)
        val normDatePattern = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)
        val normTimePattern = DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)
        val javaTimeModule = JavaTimeModule()
        javaTimeModule.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(normDatetimePattern))
        javaTimeModule.addSerializer(LocalDate::class.java, LocalDateSerializer(normDatePattern))
        javaTimeModule.addSerializer(LocalTime::class.java, LocalTimeSerializer(normTimePattern))
        javaTimeModule.addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(normDatetimePattern))
        javaTimeModule.addDeserializer(LocalDate::class.java, LocalDateDeserializer(normDatePattern))
        javaTimeModule.addDeserializer(LocalTime::class.java, LocalTimeDeserializer(normTimePattern))
        return builder
            .modules(javaTimeModule)
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build()
    }
}