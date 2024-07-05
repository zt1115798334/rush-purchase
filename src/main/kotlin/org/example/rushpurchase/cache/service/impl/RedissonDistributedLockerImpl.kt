package org.example.rushpurchase.cache.service.impl

import org.example.rushpurchase.cache.service.RedissonDistributedLocker
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import java.util.concurrent.TimeUnit

class RedissonDistributedLockerImpl(val redissonClient: RedissonClient) : RedissonDistributedLocker {

    override fun lock(lockKey: String): RLock {
        val lock = redissonClient.getLock(lockKey)
        lock.lock()
        return lock
    }

    override fun lock(lockKey: String, timeout: Long, unit: TimeUnit): RLock {
        val lock = redissonClient.getLock(lockKey)
        lock.lock(timeout, unit)
        return lock
    }

    override fun tryLock(lockKey: String, unit: TimeUnit, waitTime: Long, leaseTime: Long): Boolean {
        val lock = redissonClient.getLock(lockKey)
        return lock.tryLock(waitTime, leaseTime, unit)
    }

    override fun unlock(lockKey: String) {
        redissonClient.getLock(lockKey).unlock()
    }

    override fun unlock(lock: RLock) {
        TODO("Not yet implemented")
    }
}