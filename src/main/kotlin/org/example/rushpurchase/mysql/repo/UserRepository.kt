package org.example.rushpurchase.mysql.repo;

import org.example.rushpurchase.enums.DeleteState
import org.example.rushpurchase.mysql.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

interface UserRepository : JpaRepository<User, Long> {


    @Transactional
    @Modifying
    @Query("update User u set u.lastLoginTime = ?1 where u.id = ?2")
    fun updateLastLoginTimeById(lastLoginTime: LocalDateTime, id: Long)


    fun findByIdAndDeleteState(id: Long, deleteState: DeleteState): User?


    fun findByAccountAndDeleteState(account: String, deleteState: DeleteState): User?
}