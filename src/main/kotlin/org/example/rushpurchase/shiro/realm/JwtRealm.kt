package org.example.rushpurchase.shiro.realm

import cn.hutool.core.net.Ipv4Util
import org.apache.shiro.authc.AuthenticationInfo
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.authc.SimpleAuthenticationInfo
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.authz.SimpleAuthorizationInfo
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.subject.PrincipalCollection
import org.example.rushpurchase.cache.service.StringRedisService
import org.example.rushpurchase.enums.DeleteState
import org.example.rushpurchase.enums.SystemStatusCode
import org.example.rushpurchase.mysql.entity.User
import org.example.rushpurchase.service.UserService
import org.example.rushpurchase.shiro.cache.CacheKeys
import org.example.rushpurchase.shiro.exception.JwtAccessTokenErrorException
import org.example.rushpurchase.shiro.exception.JwtNotFunException
import org.example.rushpurchase.shiro.token.JwtToken
import org.example.rushpurchase.utils.JwtUtils
import org.springframework.stereotype.Component

@Component
class JwtRealm(val userService: UserService, val stringRedisService: StringRedisService) : AuthorizingRealm() {

    override fun getAuthenticationTokenClass(): Class<out AuthenticationToken> {
        return JwtToken::class.java
    }

    override fun doGetAuthenticationInfo(authenticationToken: AuthenticationToken?): AuthenticationInfo {
        if (authenticationToken is JwtToken) {
            val (userId, ip, token) = authenticationToken
            val ipLong = Ipv4Util.ipv4ToLong(ip)
            stringRedisService.get(CacheKeys.jwtAccessTokenKey(userId, ipLong)).let {
                userService.findUserById(userId).let {
                    if (JwtUtils.verify(token)) {
                        return SimpleAuthenticationInfo(it, token, name)
                    } else {
                        throw JwtAccessTokenErrorException()
                    }
                }
            }
        } else {
            throw JwtNotFunException()
        }
    }


    override fun doGetAuthorizationInfo(principalCollection: PrincipalCollection?): AuthorizationInfo {
        val user = principalCollection?.primaryPrincipal as User
        val permissionSet: Set<String> = HashSet()
        val info = SimpleAuthorizationInfo()
        user.let {
            if (it.deleteState == DeleteState.DELETE) {
                permissionSet.plus(SystemStatusCode.USER_DELETE.name.lowercase())
            } else {
                permissionSet.plus(SystemStatusCode.USER_NORMAL.name.lowercase())
            }
        }
        info.stringPermissions = permissionSet
        return info
    }
}

