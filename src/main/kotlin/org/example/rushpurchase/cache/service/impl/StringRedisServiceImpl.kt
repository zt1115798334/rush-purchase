package org.example.rushpurchase.cache.service.impl

import org.example.rushpurchase.cache.module.ZSetTuple
import org.example.rushpurchase.cache.service.StringRedisService
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class StringRedisServiceImpl(private val stringRedisTemplate: StringRedisTemplate) : StringRedisService {


    override fun increment(key: String, delta: Long) {
        val opsForValue = stringRedisTemplate.opsForValue()
        opsForValue.increment(key, delta)
    }

    override fun increment(key: String, delta: Long, timeout: Long, unit: TimeUnit) {
        val opsForValue = stringRedisTemplate.opsForValue()
        opsForValue.increment(key, delta)
        stringRedisTemplate.expire(key, timeout, unit)
    }

    override fun expire(key: String, timeout: Long, unit: TimeUnit) {
        stringRedisTemplate.expire(key, timeout, unit)
    }

    override fun set(key: String, value: String) {
        val opsForValue = stringRedisTemplate.opsForValue()
        opsForValue.set(key, value)
    }

    override fun set(key: String, value: String, timeout: Long, unit: TimeUnit) {
        val opsForValue = stringRedisTemplate.opsForValue()
        opsForValue.set(key, value, timeout, unit)
    }

    override fun setZSet(
        key: String,
        value: Set<ZSetTuple<String>>,
        timeout: Long,
        unit: TimeUnit
    ) {
        value.isNotEmpty().let {
            val zSetOperations = stringRedisTemplate.opsForZSet()
            value.forEach {
                zSetOperations.add(key, it.value, it.score)
            }

        }
    }

    override fun get(key: String): String? {
        val opsForValue = stringRedisTemplate.opsForValue()
        return opsForValue.get(key)
    }

    override fun getForZSetValue(key: String, score: Double): String? {
        val opsForZSet = stringRedisTemplate.opsForZSet()
        return opsForZSet.rangeByScore(key, score, score)?.first()
    }

    override fun expireTime(key: String): Long {
        return stringRedisTemplate.getExpire(key)
    }

    override fun keys(key: String): Set<String?> {
       return stringRedisTemplate.keys(key)
    }

    override fun multiGet(keys: List<String?>): List<String>? {
        val opsForValue = stringRedisTemplate.opsForValue()
        return opsForValue.multiGet(keys)
    }

    override fun delete(key: String) {
       stringRedisTemplate.delete(key)
    }

    override fun deleteForZSetValue(key: String, score: Double) {
        val opsForZSet = stringRedisTemplate.opsForZSet()
        opsForZSet.removeRangeByScore(key,score,score)
    }

    override fun deleteByLike(key: String) {
        stringRedisTemplate.delete(keys("$key*"))
    }
}