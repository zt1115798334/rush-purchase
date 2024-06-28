package org.example.rushpurchase.shiro.cache

object CacheKeys {

    fun shiroLoginCountKey(account: String): String {
        return CacheKeyConstant.PREFIX_SHIRO_LOGIN_COUNT + account
    }

    fun shiroIsLockKey(account: String): String {
        return CacheKeyConstant.PREFIX_SHIRO_IS_LOCK + account
    }

    fun jwtAccessTokenKey(userId: Long, ipLong: Long): String =
        "${CacheKeyConstant.PREFIX_JWT_ACCESS_TOKEN}:$userId:$ipLong"


    fun jwtRefreshTokenKey(userId: Long, ipLong: Long): String =
        "${CacheKeyConstant.PREFIX_JWT_REFRESH_TOKEN}:$userId:$ipLong"

}

class CacheKeyConstant {
    companion object {
        const val PREFIX_SHIRO_LOGIN_COUNT = "shiro:login_count:"
        const val PREFIX_SHIRO_IS_LOCK = "shiro:is_lock:"
        const val PREFIX_JWT_ACCESS_TOKEN = "jwt:access_token:"
        const val PREFIX_JWT_REFRESH_TOKEN = "jwt:refresh_token:"
    }
}