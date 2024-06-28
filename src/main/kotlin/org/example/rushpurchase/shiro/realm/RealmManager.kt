package org.example.rushpurchase.shiro.realm

import org.apache.shiro.realm.Realm
import org.example.rushpurchase.cache.service.StringRedisService
import org.example.rushpurchase.service.UserService
import org.example.rushpurchase.properties.AccountProperties
import org.springframework.stereotype.Component

@Component
class RealmManager(
    val userService: UserService,
    val stringRedisService: StringRedisService,
    val accountProperties: AccountProperties
) {

    fun initRealmList(): ArrayList<Realm> {
        val realmList: ArrayList<Realm> = ArrayList()
        realmList.add(PasswordRealm(userService, stringRedisService, accountProperties))
        realmList.add(JwtRealm(userService, stringRedisService))
        return realmList
    }


}