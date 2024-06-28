package org.example.rushpurchase.shiro.config

import org.apache.shiro.SecurityUtils
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator
import org.apache.shiro.mgt.DefaultSubjectDAO
import org.apache.shiro.spring.web.ShiroFilterFactoryBean
import org.apache.shiro.web.mgt.DefaultWebSecurityManager
import org.example.rushpurchase.shiro.filter.ShiroFilterChainManager
import org.example.rushpurchase.shiro.realm.AModularRealmAuthenticator
import org.example.rushpurchase.shiro.realm.RealmManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ShiroConfig {

    @Bean
    fun securityManager(realmManager: RealmManager): DefaultWebSecurityManager {

        val securityManager = DefaultWebSecurityManager()
        securityManager.authenticator = AModularRealmAuthenticator()
        securityManager.realms = realmManager.initRealmList()
        val defaultSubjectDAO = DefaultSubjectDAO()
        val defaultSessionStorageEvaluator = DefaultSessionStorageEvaluator()
        defaultSessionStorageEvaluator.isSessionStorageEnabled = false
        defaultSubjectDAO.sessionStorageEvaluator = defaultSessionStorageEvaluator
        securityManager.subjectDAO = defaultSubjectDAO
        SecurityUtils.setSecurityManager(securityManager)
        return securityManager
    }

    @Bean
    fun shiroFilterFactoryBean(
        securityManager: DefaultWebSecurityManager,
        shiroFilterChainManager: ShiroFilterChainManager
    ): ShiroFilterFactoryBean {
        val shiroFilterFactoryBean = ShiroFilterFactoryBean()
        shiroFilterFactoryBean.unauthorizedUrl = "/api/error/401"
        shiroFilterFactoryBean.securityManager = securityManager
        shiroFilterFactoryBean.filters = shiroFilterChainManager.initFilters()
        shiroFilterFactoryBean.filterChainDefinitionMap = shiroFilterChainManager.initGetFilterChain()
        return shiroFilterFactoryBean
    }
}