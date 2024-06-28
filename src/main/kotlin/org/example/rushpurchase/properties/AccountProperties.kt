package org.example.rushpurchase.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "custom.account")
data class AccountProperties(
    val firstErrorAccountLockTime: Int,
    val firstErrorAccountErrorCount: Int,
    val secondErrorAccountLockTime: Int,
    val secondErrorAccountErrorCount: Int,
    val thirdErrorAccountLockTime: Int,
    val thirdErrorAccountErrorCount: Int,
    val publicKey: String,
    val privateKey: String
)
