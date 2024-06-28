package org.example.rushpurchase.template

import com.alibaba.fastjson2.JSONObject
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

class JSONObjectRedisTemplate(connectionFactory: RedisConnectionFactory) : RedisTemplate<String, JSONObject>() {

    init {
        val stringSerializer = StringRedisSerializer()
        keySerializer = stringSerializer
        hashKeySerializer = stringSerializer
        setConnectionFactory(connectionFactory)
        afterPropertiesSet()
    }
}