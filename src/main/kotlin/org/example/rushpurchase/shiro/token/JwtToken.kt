package org.example.rushpurchase.shiro.token

import org.apache.shiro.authc.AuthenticationToken

data class JwtToken(val userId: Long, val ip: String, val token: String) : AuthenticationToken {
    override fun getPrincipal(): Any {
        return userId
    }

    override fun getCredentials(): Any {
        return token
    }
}
