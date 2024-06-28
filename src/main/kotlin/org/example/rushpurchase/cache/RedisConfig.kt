package org.example.rushpurchase.cache

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import org.example.rushpurchase.template.JSONArrayRedisTemplate
import org.example.rushpurchase.template.JSONObjectRedisTemplate
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.cache.RedisCacheWriter
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@EnableCaching
@Configuration
class RedisConfig {


    @Bean
    fun stringRedisTemplate(factory: RedisConnectionFactory): StringRedisTemplate {
        return StringRedisTemplate(factory)
    }

    @Bean
    fun redisTemplate(factory: RedisConnectionFactory, objectMapper: ObjectMapper): RedisTemplate<String,Any> {
        val template = RedisTemplate<String, Any>()
        val jackson2JsonRedisSerializer = getObjectJackson2JsonRedisSerializer(objectMapper)
        template.connectionFactory = factory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = jackson2JsonRedisSerializer
        template.hashValueSerializer = jackson2JsonRedisSerializer
        template.afterPropertiesSet()
        return template
    }

    @Bean
    fun jsonArrayRedisTemplate(factory: RedisConnectionFactory, objectMapper: ObjectMapper): JSONArrayRedisTemplate {
        val template = JSONArrayRedisTemplate(factory)
        val jackson2JsonRedisSerializer = getObjectJackson2JsonRedisSerializer(objectMapper)
        template.valueSerializer = jackson2JsonRedisSerializer
        template.hashValueSerializer = jackson2JsonRedisSerializer
        template.afterPropertiesSet()
        return template
    }

    @Bean
    fun jsonObjectRedisTemplate(
        factory: RedisConnectionFactory,
        objectMapper: ObjectMapper
    ): JSONObjectRedisTemplate {
        val template = JSONObjectRedisTemplate(factory)
        val jackson2JsonRedisSerializer = getObjectJackson2JsonRedisSerializer(objectMapper)
        template.valueSerializer = jackson2JsonRedisSerializer
        template.hashValueSerializer = jackson2JsonRedisSerializer
        template.afterPropertiesSet()
        return template
    }

    @Bean
    fun cacheManager(
        factory: RedisConnectionFactory?,
        objectMapper: ObjectMapper?
    ): CacheManager {
        return RedisCacheManager(
            RedisCacheWriter.nonLockingRedisCacheWriter(factory!!),
            this.getRedisCacheConfigurationWithTtl(6000, objectMapper!!),  // 默认策略，未配置的 key 会使用这个
            this.getRedisCacheConfigurationMap(objectMapper!!) // 指定 key 策略
        )
    }

    private fun getObjectJackson2JsonRedisSerializer(objectMapper: ObjectMapper): Jackson2JsonRedisSerializer<Any> {
        val copy = objectMapper.copy()
        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(
            objectMapper,
            Any::class.java
        )
        copy.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        copy.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        )
        return jackson2JsonRedisSerializer
    }

    private fun getRedisCacheConfigurationMap(objectMapper: ObjectMapper): Map<String, RedisCacheConfiguration> {
        val redisCacheConfigurationMap: MutableMap<String, RedisCacheConfiguration> = HashMap()
        redisCacheConfigurationMap["findUserByEffective"] = this.getRedisCacheConfigurationWithTtl(18000, objectMapper)
        return redisCacheConfigurationMap
    }

    private fun getRedisCacheConfigurationWithTtl(seconds: Int, objectMapper: ObjectMapper): RedisCacheConfiguration {
        val jackson2JsonRedisSerializer = getObjectJackson2JsonRedisSerializer(objectMapper)
        var redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(
            RedisSerializationContext
                .SerializationPair
                .fromSerializer(jackson2JsonRedisSerializer)
        ).entryTtl(Duration.ofSeconds(seconds.toLong()))

        return redisCacheConfiguration
    }

}