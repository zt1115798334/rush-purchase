package org.example.rushpurchase.controller

import org.example.rushpurchase.aop.SaveLog
import org.example.rushpurchase.base.BaseResultMessage
import org.example.rushpurchase.base.ResultMessage
import org.example.rushpurchase.dto.UserDto
import org.example.rushpurchase.dto.mapper.UserMapper
import org.example.rushpurchase.service.UserService
import org.example.rushpurchase.shiro.module.CurrentUser
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/api/user"])
class UserController(private val userService: UserService, private val userMapper: UserMapper) : BaseResultMessage(),
    CurrentUser {

    @PostMapping(value = ["/saveUser"])
    @SaveLog(desc = "保存用户")
    fun saveUser(@RequestBody userDto: UserDto): ResultMessage {
        val result = userService.saveUser(userMapper.toEntity(userDto))
        return success(userMapper.toDto(result))
    }

    @PostMapping(value = ["/test"])
    @SaveLog(desc = "保存用户")
    fun test(): ResultMessage {
        val result = userService.findUserList()
        return result?.let { success(it) } ?: success()
    }

}