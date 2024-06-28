package org.example.rushpurchase.cache.service

import org.example.rushpurchase.cache.module.ZSetTuple
import java.util.concurrent.TimeUnit

interface StringRedisService {

    fun increment(key: String, delta: Long)

    fun increment(key: String, delta: Long, timeout: Long, unit: TimeUnit)

    fun expire(key: String, timeout: Long, unit: TimeUnit)

    fun set(key: String, value: String)

    fun set(key: String, value: String, timeout: Long, unit: TimeUnit)

    fun setZSet(key: String, value: Set<ZSetTuple<String>>, timeout: Long, unit: TimeUnit)

    fun get(key: String): String?

    fun getForZSetValue(key: String, score: Double): String?

    fun expireTime(key: String): Long?

    fun keys(key: String): Set<String?>?

    fun multiGet(keys: List<String?>): List<String>?

    fun delete(key: String)

    fun deleteForZSetValue(key: String, score: Double)

    fun deleteByLike(key: String)
}