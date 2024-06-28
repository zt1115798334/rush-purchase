package org.example.rushpurchase.shiro.exception

import org.apache.shiro.authc.AuthenticationException
import org.example.rushpurchase.enums.SystemStatusCode

class JwtAccessTokenErrorException : AuthenticationException() {
    override val message: String
        get() = SystemStatusCode.JWT_ACCESS_TOKEN_ERROR.name.lowercase()
}