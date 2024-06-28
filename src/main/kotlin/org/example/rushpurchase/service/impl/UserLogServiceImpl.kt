package org.example.rushpurchase.service.impl

import org.example.rushpurchase.mysql.entity.UserLog
import org.example.rushpurchase.mysql.repo.UserLogRepository
import org.example.rushpurchase.service.UserLogService
import org.springframework.stereotype.Service

@Service
class UserLogServiceImpl(val userLogRepository: UserLogRepository) : UserLogService {
    override fun saveUserLog(userLog: UserLog) {
        userLogRepository.save(userLog)
    }
}