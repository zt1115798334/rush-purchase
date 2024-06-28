package org.example.rushpurchase.shiro.exception

import org.apache.shiro.authc.AuthenticationException
import org.example.rushpurchase.enums.SystemStatusCode

class JwtNotFunException : AuthenticationException() {
    override val message: String
        get() = SystemStatusCode.JWT_NOT_FOUND.name.lowercase()
}