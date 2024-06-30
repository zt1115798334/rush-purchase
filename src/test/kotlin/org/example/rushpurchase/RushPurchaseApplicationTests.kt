package org.example.rushpurchase

import cn.hutool.core.net.URLEncodeUtil
import cn.hutool.core.util.EscapeUtil
import cn.hutool.core.util.URLUtil
import org.example.rushpurchase.properties.AccountProperties
import org.example.rushpurchase.utils.RSAUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset

@SpringBootTest
class RushPurchaseApplicationTests(@Autowired val accountProperties: AccountProperties) {


    @Test
    fun contextLoads() {
        val publicKey = accountProperties.publicKey
        val privateKey = accountProperties.privateKey
        val encryptByPublicKey = RSAUtils.encryptByPublicKey("admin123456", publicKey)
        println("accountProperties = $encryptByPublicKey")
        val decryptByPublicKey = RSAUtils.decryptByPrivateKey(encryptByPublicKey, privateKey)
        println("decryptByPublicKey = $decryptByPublicKey")
        println(URLEncodeUtil.encode(encryptByPublicKey))
        println(EscapeUtil.escape(encryptByPublicKey))
        println(EscapeUtil.unescape(encryptByPublicKey))
        println(URLUtil.encode(encryptByPublicKey))
        println(URLUtil.decode(encryptByPublicKey))
        println(URLEncoder.encode(encryptByPublicKey, Charset.defaultCharset()))
    }

}
