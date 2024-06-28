package org.example.rushpurchase.service

import org.example.rushpurchase.mysql.entity.UserLog

interface UserLogService {

    fun saveUserLog(userLog: UserLog)

}