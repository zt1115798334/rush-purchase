package org.example.rushpurchase.service

import org.example.rushpurchase.dto.UserDto
import org.example.rushpurchase.mysql.entity.User

interface UserService {

    fun saveUser(user: User): User

    fun findUserList(): MutableList<UserDto>?

    fun findUserById(id: Long) : User?

    fun findUserByAccount(account:String):User?

    fun updateLastLoginTime(id: Long)

}