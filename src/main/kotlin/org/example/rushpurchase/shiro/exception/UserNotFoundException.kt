package org.example.rushpurchase.shiro.exception

import org.apache.shiro.authc.AuthenticationException
import org.example.rushpurchase.enums.SystemStatusCode

class UserNotFoundException(message: String?) : AuthenticationException(message) {
    override val message: String
        get() = SystemStatusCode.USER_NOT_FOUND.name.lowercase()
}