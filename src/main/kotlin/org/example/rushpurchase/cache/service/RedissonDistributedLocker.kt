package org.example.rushpurchase.cache.service

import org.redisson.api.RLock
import java.util.concurrent.TimeUnit

interface RedissonDistributedLocker {

    fun lock(lockKey: String): RLock

    fun lock(lockKey: String, timeout: Long, unit: TimeUnit): RLock

    fun tryLock(lockKey: String, unit: TimeUnit, waitTime: Long, leaseTime: Long): Boolean

    fun unlock(lockKey: String)

    fun unlock(lock: RLock)


}