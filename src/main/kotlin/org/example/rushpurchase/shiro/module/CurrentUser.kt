package org.example.rushpurchase.shiro.module

import org.apache.shiro.SecurityUtils
import org.example.rushpurchase.mysql.entity.User

interface CurrentUser {
    fun getCurrentUser() = SecurityUtils.getSubject().principal as User
    fun getCurrentUserId() = getCurrentUser().id
}