package org.example.rushpurchase.service.impl

import cn.hutool.core.net.Ipv4Util
import org.apache.shiro.SecurityUtils
import org.example.rushpurchase.cache.service.StringRedisService
import org.example.rushpurchase.mysql.entity.User
import org.example.rushpurchase.service.LoginService
import org.example.rushpurchase.shiro.cache.CacheKeys
import org.example.rushpurchase.shiro.token.PasswordToken
import org.example.rushpurchase.utils.JwtConstant
import org.example.rushpurchase.utils.JwtUtils
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class LoginServiceImpl(val stringRedisService: StringRedisService) : LoginService {

    override fun login(token: PasswordToken): String {
        val ipLong = Ipv4Util.ipv4ToLong(token.ip)
        SecurityUtils.getSubject().login(token)
        val user = SecurityUtils.getSubject().principal as User
        val userId = user.id
        val accessToken = JwtUtils.generateAccessToken(user)
        val refreshToken = JwtUtils.generateRefreshToken(user)

        userId?.let { CacheKeys.jwtAccessTokenKey(it, ipLong) }
            ?.let { stringRedisService.set(it, accessToken, JwtConstant.ACCESS_EXPIRATION.toLong(), TimeUnit.HOURS) }

        userId?.let { CacheKeys.jwtRefreshTokenKey(it, ipLong) }
            ?.let { stringRedisService.set(it, refreshToken, JwtConstant.REFRESH_EXPIRATION.toLong(), TimeUnit.HOURS) }
        return accessToken
    }

    override fun logout(userId: Long?, ip: String) {
        val ipLong = Ipv4Util.ipv4ToLong(ip)
        userId?.let { CacheKeys.jwtAccessTokenKey(it, ipLong) }
            ?.let { stringRedisService.delete(it) }

        userId?.let { CacheKeys.jwtRefreshTokenKey(it, ipLong) }
            ?.let { stringRedisService.delete(it) }
        SecurityUtils.getSubject().logout()
    }
}