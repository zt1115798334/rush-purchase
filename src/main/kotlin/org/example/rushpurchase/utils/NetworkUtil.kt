package org.example.rushpurchase.utils

import jakarta.servlet.http.HttpServletRequest

object NetworkUtil {

    private fun isNotIP(ip: String?): Boolean =
        ip.let { "unknown".equals(it, ignoreCase = true) }

    fun getLocalIp(servletRequest: HttpServletRequest): String {
        var first = listOf(
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
        ).mapNotNull {
            servletRequest.getHeader(it)
        }.firstOrNull {
            isNotIP(it)
        }

        if (isNotIP(first)) {
            first = servletRequest.remoteAddr
        }
        return first?.let { f ->
            return if (f.contains(",")) {
                f.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].trim { it <= ' ' }
            } else {
                "127.0.0.1"
            }
        } ?: "127.0.0.1"


    }
}