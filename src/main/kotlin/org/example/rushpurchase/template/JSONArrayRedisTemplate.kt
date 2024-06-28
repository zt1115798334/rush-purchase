package org.example.rushpurchase.template

import com.alibaba.fastjson2.JSONArray
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

class JSONArrayRedisTemplate(connectionFactory: RedisConnectionFactory) : RedisTemplate<String, JSONArray>() {
    init {
        val stringSerializer = StringRedisSerializer()
        keySerializer = stringSerializer
        hashKeySerializer = stringSerializer
        setConnectionFactory(connectionFactory)
        afterPropertiesSet()
    }
}