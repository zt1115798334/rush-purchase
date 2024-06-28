package org.example.rushpurchase.shiro.token

import org.apache.shiro.authc.UsernamePasswordToken

data class PasswordToken(val  usernamePasswordToken: UsernamePasswordToken, val ip: String) :
    UsernamePasswordToken(usernamePasswordToken.username, usernamePasswordToken.password)
