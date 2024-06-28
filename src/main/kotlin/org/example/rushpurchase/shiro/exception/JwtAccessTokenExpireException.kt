package org.example.rushpurchase.shiro.exception

import org.apache.shiro.authc.AuthenticationException
import org.example.rushpurchase.enums.SystemStatusCode

class JwtAccessTokenExpireException(message: String?) : AuthenticationException(message) {
    override val message: String
        get() = SystemStatusCode.JWT_ACCESS_TOKEN_EXPIRE.name.lowercase()
}