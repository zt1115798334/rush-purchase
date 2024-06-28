package org.example.rushpurchase.cache.service.impl

import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import org.example.rushpurchase.cache.service.RedisService
import org.example.rushpurchase.template.JSONArrayRedisTemplate
import org.example.rushpurchase.template.JSONObjectRedisTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisServiceImpl(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val jsonArrayRedisTemplate: JSONArrayRedisTemplate,
    private val jsonObjectRedisTemplate: JSONObjectRedisTemplate
) : RedisService {

    override fun getObject(key: String): Any? {
        return redisTemplate.opsForValue().get(key)
    }

    override fun getJSONObject(key: String): JSONObject? {
        return jsonObjectRedisTemplate.opsForValue().get(key)
    }

    override fun getJSONArray(key: String): JSONArray? {
        return jsonArrayRedisTemplate.opsForValue().get(key)
    }

    override fun setObject(key: String, value: Any) {
        redisTemplate.opsForValue().set(key, value)
    }

    override fun setObject(key: String, value: Any, timeout: Long, unit: TimeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit)
    }

    override fun setJSONObject(key: String, value: JSONObject) {
        jsonObjectRedisTemplate.opsForValue().set(key, value)
    }

    override fun setJSONObject(key: String, value: JSONObject, timeout: Long, unit: TimeUnit) {
        jsonObjectRedisTemplate.opsForValue().set(key, value, timeout, unit)
    }

    override fun setJSONArray(key: String, value: JSONArray) {
        jsonArrayRedisTemplate.opsForValue().set(key, value)
    }

    override fun setJSONArray(key: String, value: JSONArray, timeout: Long, unit: TimeUnit) {
        jsonArrayRedisTemplate.opsForValue().set(key, value, timeout, unit)
    }

    override fun delete(key: String): Boolean {
        return redisTemplate.delete(key)
    }
}