package org.example.rushpurchase.aop

import cn.hutool.core.convert.Convert
import cn.hutool.core.date.DateUtil
import cn.hutool.core.date.TimeInterval
import com.alibaba.fastjson2.JSONObject
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.example.rushpurchase.mysql.entity.User
import org.example.rushpurchase.mysql.entity.UserLog
import org.example.rushpurchase.service.UserLogService
import org.example.rushpurchase.shiro.module.CurrentUser
import org.example.rushpurchase.utils.NetworkUtil
import org.example.rushpurchase.utils.ParamsUtils
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.LocalDateTime
import java.util.*

@Aspect
@Component
class SaveLogAspect(val userLogService: UserLogService) : CurrentUser {
    private val className = ThreadLocal<String>()
    private val methodName = ThreadLocal<String>()
    private val paramVal = ThreadLocal<String>()
    private val ip = ThreadLocal<String>()
    private val userId = ThreadLocal<Long>()
    private val userName = ThreadLocal<String>()
    private val timeConsuming = ThreadLocal<TimeInterval>()

    @Pointcut("execution( * org.example.rushpurchase.controller..*.*(..)) && @annotation(logs)")
    fun aopPointCut(logs: SaveLog): Unit = Unit

    @Before(value = "aopPointCut(logs)", argNames = "joinPoint,logs")
    private fun doBefore(joinPoint: JoinPoint, logs: SaveLog) {
        val signature = joinPoint.signature
        className.set(signature.declaringTypeName)
        methodName.set(signature.name)
        val methodSignature = Convert.convert(
            MethodSignature::class.java, signature
        )

        val parameterValues = joinPoint.args
        val parameterNames = methodSignature.parameterNames
        paramVal.set((ParamsUtils.parseParams(parameterNames, parameterValues)))
        val request =
            (Objects.requireNonNull(RequestContextHolder.getRequestAttributes()) as ServletRequestAttributes).request
        ip.set(NetworkUtil.getLocalIp(request))
        timeConsuming.set(DateUtil.timer())
        if (logs.containUser) {
            val currentUser: User = getCurrentUser()
            userId.set(currentUser.id)
            userName.set(currentUser.userName)
        }

    }

    @AfterReturning(returning = "object", pointcut = "aopPointCut(logs)", argNames = "object,logs")
    private fun doAfterReturning(obj: Any, logs: SaveLog) {
        saveUserLog(logs.desc, JSONObject.toJSONString(obj),timeConsuming.get().interval())

        className.remove()
        paramVal.remove()
        methodName.remove()
        ip.remove()
        userId.remove()
        userName.remove()
        timeConsuming.remove()
    }

    private fun saveUserLog(type: String, response: String,timeConsuming:Long) {
        val userLog = UserLog()
        userLog.userId = userId.get()
        userLog.name = userName.get()
        userLog.type = type
        userLog.content = paramVal.get()
        userLog.ip = ip.get()
        userLog.createdTime = LocalDateTime.now()
        userLog.classify = className.get()
        userLog.func = methodName.get()
        userLog.resp = response
        userLog.timeConsuming = timeConsuming

        userLogService.saveUserLog(userLog)
    }

}