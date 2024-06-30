package org.example.rushpurchase.utils

import cn.hutool.crypto.asymmetric.KeyType
import cn.hutool.crypto.asymmetric.RSA

object RSAUtils {

    fun encryptByPrivateKey(data: String, privateKey: String): String {
        val rsa = RSA(privateKey, null)
        return rsa.encryptBase64(data, KeyType.PrivateKey)
    }

    fun decryptByPrivateKey(data: String, privateKey: String): String? {
        val rsa = RSA(privateKey, null)
        return rsa.decryptStr(data, KeyType.PrivateKey)
    }

    fun encryptByPublicKey(data: String, publicKey: String): String {
        val rsa = RSA(null, publicKey)
        return rsa.encryptBase64(data, KeyType.PublicKey)
    }

    fun decryptByPublicKey(data: String, publicKey: String): String {
        val rsa = RSA(null, publicKey)
        return rsa.decryptStr(data, KeyType.PublicKey)
    }
}
