package org.example.rushpurchase.mysql.entity

import jakarta.persistence.*
import org.example.rushpurchase.enums.DeleteState
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime

@Entity
@Table(name = "t_user")
open class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "account", nullable = false, length = 20)
    open var account: String? = null

    @Column(name = "password", nullable = false)
    open var password: String? = null

    @Column(name = "salt", nullable = false)
    open var salt: String? = null

    @Column(name = "user_name", length = 20)
    open var userName: String? = null

    @Column(name = "phone", nullable = false, length = 20)
    open var phone: String? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_time", nullable = false)
    open var createdTime: LocalDateTime? = null

    @Column(name = "updated_time")
    open var updatedTime: LocalDateTime? = null

    @Column(name = "last_login_time")
    open var lastLoginTime: LocalDateTime? = null

    @ColumnDefault("UN_DELETE")
    @Enumerated(value = EnumType.STRING)
    @Column(name = "delete_state", nullable = false)
    open var deleteState: DeleteState? = null
}