package org.example.rushpurchase.mysql.repo

import org.example.rushpurchase.mysql.entity.UserLog
import org.springframework.data.jpa.repository.JpaRepository

interface UserLogRepository : JpaRepository<UserLog, Long> {
}