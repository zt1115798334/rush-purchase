package org.example.rushpurchase.aop


@kotlin.annotation.Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class SaveLog(val desc:String = "无信息",val containUser:Boolean = true)
