package org.example.rushpurchase.dto

import java.io.Serializable

class UserDto(
    var id: Long? = null,
    var account: String? = null,
    var password: String? = null,
    var userName: String? = null,
    var phone: String? = null
) : Serializable