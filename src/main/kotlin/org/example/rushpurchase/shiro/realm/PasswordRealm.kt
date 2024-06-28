package org.example.rushpurchase.shiro.realm

import cn.hutool.core.date.BetweenFormatter
import cn.hutool.core.date.DateUtil
import org.apache.shiro.authc.*
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.subject.PrincipalCollection
import org.example.rushpurchase.cache.service.StringRedisService
import org.example.rushpurchase.service.UserService
import org.example.rushpurchase.properties.AccountProperties
import org.example.rushpurchase.shiro.cache.CacheKeys
import org.example.rushpurchase.shiro.token.PasswordToken
import org.example.rushpurchase.utils.RSAUtils
import org.example.rushpurchase.utils.UserUtils
import java.util.concurrent.TimeUnit

class PasswordRealm(
    private val userService: UserService,
    private val stringRedisService: StringRedisService,
    private val accountProperties: AccountProperties
) : AuthorizingRealm() {

    override fun getAuthenticationTokenClass(): Class<out AuthenticationToken> {
        return PasswordToken::class.java
    }

    override fun doGetAuthenticationInfo(authenticationToken: AuthenticationToken?): AuthenticationInfo? {
        if (authenticationToken is PasswordToken) {
            val username = authenticationToken.username
            val shiroIsLockKey = CacheKeys.shiroIsLockKey(username)
            val shiroLoginCountKey = CacheKeys.shiroLoginCountKey(username)
            stringRedisService.get(shiroIsLockKey)?.let {
                if ("LOCK" == it) {
                    val expireTime = stringRedisService.expireTime(shiroIsLockKey)
                    throw DisabledAccountException(
                        "由于多次输入错误密码，帐号已经禁止登录，请${
                            DateUtil.formatBetween(
                                expireTime!! * 1000L, BetweenFormatter.Level.SECOND
                            )
                        }后重新尝试。"
                    )
                }
            }

            val decode =
                RSAUtils.decryptByPrivateKey(String(authenticationToken.password), accountProperties.privateKey)
            decode?.let {
                val password = it.substring(username.length)
                val user = userService.findUserByAccount(username)
                user?.let {
                    if (UserUtils.checkUserDeleteState(user)) {
                        val msg = increment(shiroLoginCountKey, shiroIsLockKey)
                        throw AccountException(msg)
                    }
                    if (UserUtils.getVerifyPassword(username, password, user.salt, user.password).not()) {
                        val msg = increment(shiroLoginCountKey, shiroIsLockKey)
                        throw AccountException(msg)
                    }
                    user.id?.let {
                        userService.updateLastLoginTime(it)
                    }
                    stringRedisService.set(shiroLoginCountKey, "0")
                    stringRedisService.delete(shiroIsLockKey)
                    return SimpleAuthenticationInfo(user, authenticationToken.password, name)
                } ?: {
                    val msg = increment(shiroLoginCountKey, shiroIsLockKey)
                    throw AccountException(msg)
                }
            } ?: {
                val msg = increment(shiroLoginCountKey, shiroIsLockKey)
                throw AccountException(msg)
            }


        }
        return null
    }

    override fun doGetAuthorizationInfo(principalCollection: PrincipalCollection?): AuthorizationInfo? {
        return null
    }

    private fun increment(shiroLoginCountKey: String, shiroIsLockKey: String): String {
        stringRedisService.increment(shiroLoginCountKey, 1)
        return stringRedisService.get(shiroIsLockKey)
            ?.let {
                val firstErrorAccountErrorCount = accountProperties.firstErrorAccountErrorCount
                val secondErrorAccountErrorCount = accountProperties.secondErrorAccountErrorCount
                val thirdErrorAccountErrorCount = accountProperties.thirdErrorAccountErrorCount
                val toInt = it.toInt()
                val count = when {
                    toInt <= firstErrorAccountErrorCount -> firstErrorAccountErrorCount - toInt
                    toInt <= secondErrorAccountErrorCount -> secondErrorAccountErrorCount - toInt
                    toInt <= thirdErrorAccountErrorCount -> thirdErrorAccountErrorCount - toInt
                    else -> 0
                }

                val lockTime = when {
                    toInt == firstErrorAccountErrorCount -> accountProperties.firstErrorAccountLockTime.toLong()
                    toInt == secondErrorAccountErrorCount -> accountProperties.secondErrorAccountLockTime.toLong()
                    toInt >= thirdErrorAccountErrorCount -> accountProperties.thirdErrorAccountLockTime.toLong()
                    else -> 0L
                }
                stringRedisService.set(shiroIsLockKey, "LOCK", lockTime, TimeUnit.MINUTES)
                return "帐号，密码或验证码错误！你还可以输入${count + 1}次"
            } ?: "帐号，密码或验证码错误！"
    }
}