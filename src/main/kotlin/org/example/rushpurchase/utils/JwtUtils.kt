package org.example.rushpurchase.utils

import cn.hutool.core.date.DateField
import cn.hutool.core.date.DateTime
import cn.hutool.jwt.JWTPayload
import cn.hutool.jwt.JWTUtil
import org.example.rushpurchase.mysql.entity.User

object JwtUtils {

    private fun generateExpirationDate(expiration: Int, dateField: DateField?): DateTime =
        DateTime.now().offsetNew(dateField, expiration)

    fun verify(token: String) = JWTUtil.parseToken(token).setKey(JwtConstant.TOKEN_KEY.toByteArray()).verify()

    fun generateAccessToken(user: User) = generateToken(user, JwtConstant.ACCESS_EXPIRATION, DateField.HOUR)

    fun generateRefreshToken(user: User) = generateToken(user, JwtConstant.REFRESH_EXPIRATION, DateField.HOUR)

    private fun generateToken(user: User, expiration: Int, dateField: DateField?): String {
        val expirationDate = generateExpirationDate(expiration, dateField)
        val now = DateTime.now()
        val payload: MutableMap<String, Any> = HashMap()
        payload[JWTPayload.ISSUED_AT] = now
        payload[JWTPayload.EXPIRES_AT] = expirationDate
        payload[JWTPayload.NOT_BEFORE] = now

        payload[JwtConstant.TOKEN_CLAIM] = user.id.toString()
        payload[JwtConstant.TOKEN_CLAIM_ACCOUNT] = user.account.toString()
        return JWTUtil.createToken(payload, JwtConstant.TOKEN_KEY.toByteArray())
    }

    fun getUserId(token: String?) = JWTUtil.parseToken(token).getPayload(JwtConstant.TOKEN_CLAIM).toString().toLong()
}

class JwtConstant {
    companion object {
        const val TOKEN_KEY = "key"
        const val TOKEN_CLAIM = "userId"
        const val TOKEN_CLAIM_ACCOUNT = "account"
        const val ACCESS_EXPIRATION = 1
        const val REFRESH_EXPIRATION = 6
    }
}