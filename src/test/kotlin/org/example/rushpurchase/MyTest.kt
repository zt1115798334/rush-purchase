package org.example.rushpurchase

import cn.hutool.core.util.CharsetUtil
import cn.hutool.core.util.RandomUtil
import cn.hutool.core.util.URLUtil
import cn.hutool.crypto.SecureUtil
import com.alibaba.fastjson2.JSONObject
import org.example.rushpurchase.base.ResultMessage
import org.example.rushpurchase.utils.RSAUtils
import org.example.rushpurchase.utils.UserUtils
import org.junit.jupiter.api.Test
import java.net.URLDecoder
import java.net.URLEncoder
import java.time.LocalDateTime

class MyTest {

    @Test
    fun RsaUtilsTest() {
        val rsa = SecureUtil.rsa()
        val privateKey = rsa.privateKeyBase64
        val publicKey = rsa.publicKeyBase64
        println("privateKey = $privateKey")
        println("publicKey = $publicKey")

        val data = "1234"
        val encryptByPublicKey = RSAUtils.encryptByPublicKey(data, publicKey)
        println("encryptByPublicKey = $encryptByPublicKey")
        val decryptByPrivateKey = RSAUtils.decryptByPrivateKey(encryptByPublicKey, privateKey)
        println("decryptByPrivateKey = $decryptByPrivateKey")
    }

    @Test
    fun queryPersonalState() {
        val salt = RandomUtil.randomStringUpper(10)
        val encryptPassword = UserUtils.getEncryptPassword("admin", salt, "123456")
        println("salt = $salt")
        println("encryptPassword = $encryptPassword")
    }

    @Test
    fun testURL() {
        val salt = """
        mk+yx7Fsq0VS6Mtp5frc6ZCaz3wR0gzh2/yCCKFBkepx1jFVMWnxdjFc04UrFoTKm2XnaIi+Q8Ku656uUbSbtFc6D1ytwQgqhq7Z8V8Jj+6D0KBdc3P9JboocLVcDGeU0/MmVAlS37eRiRExWhy6/vUaOsQx2EG/KI1nptK4spQ=
        """.trimIndent()
        val decode = URLEncoder.encode(salt,CharsetUtil.defaultCharset())
        println(decode)
        println(URLDecoder.decode(decode,CharsetUtil.defaultCharset()))
    }


    @Test
    fun getVerifyPassword() {
        val account = "admin"
        val password = "123456"
        val salt = "9OSGNBM9WS"
        val pwd = "578d979f5571eed96e0ec077802a03e88c41cebe2ce8b80efbd30effc33dae3b"
        println(UserUtils.getVerifyPassword(account, password, salt, pwd))

    }

    @Test
    fun ttt() {
        val meta = ResultMessage.Meta(true, 20, LocalDateTime.now(), "ddd")
        println(JSONObject.toJSONString(meta))
    }
}