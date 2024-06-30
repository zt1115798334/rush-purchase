package org.example.rushpurchase.service.impl

import cn.hutool.core.util.RandomUtil
import com.blazebit.persistence.CriteriaBuilderFactory
import com.blazebit.persistence.querydsl.BlazeJPAQuery
import com.querydsl.core.types.Projections
import jakarta.persistence.EntityManager
import org.example.rushpurchase.dto.UserDto
import org.example.rushpurchase.enums.DeleteState
import org.example.rushpurchase.mysql.entity.QUser
import org.example.rushpurchase.mysql.entity.User
import org.example.rushpurchase.mysql.repo.UserRepository
import org.example.rushpurchase.service.UserService
import org.example.rushpurchase.utils.UserUtils
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class UserServiceImpl(
    val userRepository: UserRepository,
    val entityManager: EntityManager,
    val criteriaBuilderFactory: CriteriaBuilderFactory
) : UserService {

    override fun saveUser(user: User): User {
        val userId = user.id
        return userId?.let {
            val userOptional = userRepository.findById(it)
            val userDb = userOptional.orElseThrow {
                throw RuntimeException("已删除")
            }
            userDb.userName = user.userName
            userDb.phone = user.phone
            userDb.updatedTime = LocalDateTime.now()
            userRepository.save(userDb)
        } ?: run {
            val salt = RandomUtil.randomStringUpper(10)
            val encryptPassword = UserUtils.getEncryptPassword("admin", salt, "123456")
            user.password = encryptPassword
            user.salt = salt
            user.createdTime = LocalDateTime.now()
            user.deleteState = DeleteState.UN_DELETE
            userRepository.save(user)
        }


    }

    override fun findUserList(): MutableList<UserDto> {
        val qUser = QUser.user
        val jpaQuery = BlazeJPAQuery<UserDto>(entityManager, criteriaBuilderFactory)
            .select(
                Projections.bean(
                    UserDto::class.java,
                    qUser.id,
                    qUser.userName,
                    qUser.phone,
                    qUser.lastLoginTime,
                    qUser.createdTime
                )
            ).from(qUser)
        return jpaQuery.fetch()
    }

    override fun findUserById(id: Long): User? {
        return userRepository.findByIdAndDeleteState(id, DeleteState.UN_DELETE)
    }

    override fun findUserByAccount(account: String): User? {
        return userRepository.findByAccountAndDeleteState(account, DeleteState.UN_DELETE)
    }

    override fun updateLastLoginTime(id: Long) {
        userRepository.updateLastLoginTimeById(LocalDateTime.now(), id)
    }
}