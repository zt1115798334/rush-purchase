package org.example.rushpurchase.aop


@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SaveLog(val desc:String = "无信息",val containUser:Boolean = true)
