package org.example.rushpurchase.mysql.entity

import jakarta.persistence.*
import org.example.rushpurchase.enums.DeleteState
import org.hibernate.annotations.ColumnDefault
import java.time.Instant
import java.time.LocalDateTime

@Entity
@Table(name = "t_user_log")
open class UserLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "user_id")
    open var userId: Long? = null

    @Column(name = "name")
    open var name: String? = null

    @Column(name = "type")
    open var type: String? = null

    @Lob
    @Column(name = "content")
    open var content: String? = null

    @Column(name = "ip", length = 20)
    open var ip: String? = null

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_time")
    open var createdTime: LocalDateTime? = null

    @Column(name = "classify")
    open var classify: String? = null

    @Column(name = "func")
    open var func: String? = null

    @Lob
    @Column(name = "resp")
    open var resp: String? = null

    @Column(name = "time_consuming")
    open var timeConsuming: Long? = null
}