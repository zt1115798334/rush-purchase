package org.example.rushpurchase.shiro.filter

import jakarta.servlet.Filter
import org.example.rushpurchase.cache.service.StringRedisService
import org.example.rushpurchase.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ShiroFilterChainManager(
    @Autowired val userService: UserService,
    @Autowired val stringRedisService: StringRedisService
) {


    fun initFilters(): Map<String, Filter> {
        val filter: MutableMap<String, Filter> = HashMap()
        val jwtFilter = JwtFilter(userService, stringRedisService)
        filter["JwtFilter"] = jwtFilter
        return filter
    }

    fun initGetFilterChain(): MutableMap<String, String> {
        val filterChain: MutableMap<String, String> = LinkedHashMap()
        filterChain["/api/login/login"] = "anon"
        filterChain["/api/file/**"] = "anon"
        filterChain["/api/**"] = "JwtFilter"
        return filterChain
    }

}