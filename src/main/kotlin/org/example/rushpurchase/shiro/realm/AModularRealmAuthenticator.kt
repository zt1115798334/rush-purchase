package org.example.rushpurchase.shiro.realm

import org.apache.shiro.authc.AuthenticationInfo
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.authc.pam.ModularRealmAuthenticator
import org.apache.shiro.realm.Realm

class AModularRealmAuthenticator : ModularRealmAuthenticator() {
    override fun doAuthenticate(authenticationToken: AuthenticationToken?): AuthenticationInfo {
        assertRealmsConfigured()
        val realms = realms.filter { realm: Realm? -> realm?.supports(authenticationToken) ?: false }
            .toList()
        return if (realms.size == 1) {
            this.doSingleRealmAuthentication(realms.iterator().next(), authenticationToken)
        } else {
            this.doMultiRealmAuthentication(realms, authenticationToken)
        }
    }
}