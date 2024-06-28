package org.example.rushpurchase.utils

import cn.hutool.core.util.HexUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.crypto.SmUtil
import cn.hutool.crypto.digest.SM3
import org.example.rushpurchase.enums.DeleteState
import org.example.rushpurchase.mysql.entity.User

object UserUtils {

    fun checkUserDeleteState(user: User): Boolean = user.deleteState == DeleteState.DELETE

    fun getVerifyPassword(account: String, password: String, salt: String?, passSm: String?) =
        StrUtil.equals(HexUtil.encodeHexStr(SM3().digest(account + password + salt)), passSm)

    fun  getEncryptPassword(account: String, salt: String?, password: String): String =
        SmUtil.sm3(account + password + salt)
}