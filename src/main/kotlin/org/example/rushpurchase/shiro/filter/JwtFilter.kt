package org.example.rushpurchase.shiro.filter

import cn.hutool.core.net.NetUtil
import com.alibaba.fastjson2.JSONObject
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter
import org.example.rushpurchase.base.ResultMessage
import org.example.rushpurchase.base.ResultMessage.Util
import org.example.rushpurchase.cache.service.StringRedisService
import org.example.rushpurchase.enums.SystemStatusCode
import org.example.rushpurchase.service.UserService
import org.example.rushpurchase.shiro.cache.CacheKeys
import org.example.rushpurchase.shiro.exception.JwtAccessTokenErrorException
import org.example.rushpurchase.shiro.exception.JwtAccessTokenExpireException
import org.example.rushpurchase.shiro.exception.JwtNotFunException
import org.example.rushpurchase.shiro.exception.UserNotFoundException
import org.example.rushpurchase.shiro.token.JwtToken
import org.example.rushpurchase.utils.JwtConstant
import org.example.rushpurchase.utils.JwtUtils
import org.example.rushpurchase.utils.NetworkUtil
import org.example.rushpurchase.utils.RequestResponseUtil
import java.util.concurrent.TimeUnit

class JwtFilter(private val userService: UserService, private val stringRedisService: StringRedisService) :
    BasicHttpAuthenticationFilter() {

    override fun isAccessAllowed(request: ServletRequest?, response: ServletResponse?, mappedValue: Any?): Boolean {
        val subject = getSubject(request, response)
        return subject != null && subject.isAuthenticated
    }

    override fun onAccessDenied(request: ServletRequest?, response: ServletResponse?): Boolean {
        try {
            val token = createToken(request as HttpServletRequest)
            getSubject(request, response).login(token)
            return true
        } catch (e: AuthenticationException) {
            if (e is JwtAccessTokenExpireException) {
                if (!refreshToken(request as HttpServletRequest, response as HttpServletResponse)) {
                    val resultMessage = ResultMessage(
                        Util.errorMeta(
                            SystemStatusCode.JWT_EXPIRE.code,
                            SystemStatusCode.JWT_EXPIRE.name.lowercase()
                        )
                    )
                    RequestResponseUtil.responseWrite(response, JSONObject.toJSONString(resultMessage))
                }
            }
            if (e is JwtAccessTokenErrorException) {
                val resultMessage = ResultMessage(
                    Util.errorMeta(
                        SystemStatusCode.JWT_ERROR.code,
                        SystemStatusCode.JWT_ERROR.name.lowercase()
                    )
                )
                RequestResponseUtil.responseWrite(
                    response as HttpServletResponse,
                    JSONObject.toJSONString(resultMessage)
                )
            }
            if (e is JwtNotFunException) {
                val resultMessage = ResultMessage(
                    Util.errorMeta(
                        SystemStatusCode.JWT_NOT_FOUND.code,
                        SystemStatusCode.JWT_NOT_FOUND.name.lowercase()
                    )
                )
                RequestResponseUtil.responseWrite(
                    response as HttpServletResponse,
                    JSONObject.toJSONString(resultMessage)
                )
            }
            if (e is UserNotFoundException) {
                val resultMessage = ResultMessage(
                    Util.errorMeta(
                        SystemStatusCode.USER_NOT_FOUND.code,
                        SystemStatusCode.USER_NOT_FOUND.name.lowercase()
                    )
                )
                RequestResponseUtil.responseWrite(
                    response as HttpServletResponse,
                    JSONObject.toJSONString(resultMessage)
                )
            }
        }
        return false
    }

    private fun createToken(servletRequest: HttpServletRequest): AuthenticationToken? {
        val requestHeader = RequestResponseUtil.getRequestHeader(servletRequest)
        val ip = NetworkUtil.getLocalIp(servletRequest)
        val token = requestHeader["authorization"]?.substring(7)
        val userId = JwtUtils.getUserId(token)
        return token?.let { JwtToken(userId, ip, it) }
    }

    private fun refreshToken(servletRequest: HttpServletRequest, response: HttpServletResponse): Boolean {
        val requestHeader = RequestResponseUtil.getRequestHeader(servletRequest)
        val ip = NetworkUtil.getLocalIp(servletRequest)
        val ipLong = NetUtil.ipv4ToLong(ip)
        val token = requestHeader["authorization"]?.substring(7)
        val userId = JwtUtils.getUserId(token)
        val jwtAccessTokenKey = CacheKeys.jwtAccessTokenKey(userId, ipLong)
        val jwtRefreshTokenKey = CacheKeys.jwtRefreshTokenKey(userId, ipLong)
        stringRedisService.get(jwtRefreshTokenKey).let {
            userService.findUserById(userId)?.let { user ->
                val accessToken = JwtUtils.generateAccessToken(user)
                val refreshToken = JwtUtils.generateRefreshToken(user)
                stringRedisService.set(
                    jwtAccessTokenKey,
                    accessToken,
                    JwtConstant.ACCESS_EXPIRATION.toLong(),
                    TimeUnit.HOURS
                )
                stringRedisService.set(
                    jwtRefreshTokenKey,
                    refreshToken,
                    JwtConstant.ACCESS_EXPIRATION.toLong(),
                    TimeUnit.HOURS
                )
                val resultMessage = ResultMessage(
                    meta = Util.successMeta(
                        SystemStatusCode.JWT_NEW.code,
                        SystemStatusCode.JWT_NEW.name.lowercase()
                    ), obj = JSONObject.of("accessToken", accessToken)
                )
                RequestResponseUtil.responseWrite(response, JSONObject.toJSONString(resultMessage))
                return true
            }
        }
        return false
    }
}