package org.example.rushpurchase.controller

import jakarta.servlet.http.HttpServletRequest
import org.apache.shiro.authc.UsernamePasswordToken
import org.example.rushpurchase.aop.SaveLog
import org.example.rushpurchase.base.BaseResultMessage
import org.example.rushpurchase.base.ResultMessage
import org.example.rushpurchase.service.LoginService
import org.example.rushpurchase.shiro.module.CurrentUser
import org.example.rushpurchase.shiro.token.PasswordToken
import org.example.rushpurchase.utils.NetworkUtil
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/api/login"])
class LoginController(private val loginService: LoginService) : BaseResultMessage(), CurrentUser {

    @SaveLog(desc = "登录", containUser = false)
    @PostMapping(value = ["/login"])
    fun login(
        request: HttpServletRequest,
        @RequestParam username: String, @RequestParam password: String
    ): ResultMessage {
        val ip = NetworkUtil.getLocalIp(request)
        val accessToken = loginService.login(PasswordToken(UsernamePasswordToken(username, password), ip))
        return success(accessToken)
    }

    @SaveLog(desc = "注销")
    @PostMapping(value = ["/logout"])
    fun logout(request: HttpServletRequest): ResultMessage {
        val ip = NetworkUtil.getLocalIp(request)
        loginService.logout(getCurrentUserId(), ip)
        return success()

    }
}