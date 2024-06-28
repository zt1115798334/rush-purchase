package org.example.rushpurchase.enums

enum class SystemStatusCode(val code: Int) {
    SUCCESS(200),
    FAILURE(400),

    USER_NORMAL(1000),
    USER_DELETE(1003),
    USER_NOT_FOUND(1004),

    JWT_NEW(3000),
    JWT_ACCESS_TOKEN_EXPIRE(30001),
    JWT_ACCESS_TOKEN_ERROR(30002),
    JWT_EXPIRE(3001),
    JWT_ERROR(3002),
    JWT_NOT_FOUND(3003);

    companion object {
        fun fromCode(code: Int): SystemStatusCode? {
            return entries.firstOrNull { it.code == code }
        }
    }
}