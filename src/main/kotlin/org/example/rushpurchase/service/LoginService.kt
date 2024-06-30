package org.example.rushpurchase.service

import org.example.rushpurchase.mysql.entity.User
import org.example.rushpurchase.shiro.token.PasswordToken

interface LoginService {
    fun login(token: PasswordToken): String

    fun logout(userId: Long?, ip: String)
}