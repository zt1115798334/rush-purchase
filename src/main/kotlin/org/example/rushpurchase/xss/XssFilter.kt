package org.example.rushpurchase.xss

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.*
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.annotation.Order

@Order(1)
@WebFilter(filterName = "xssFilter", urlPatterns = ["/*"], asyncSupported = true)
class XssFilter : Filter {
    private val log = KotlinLogging.logger {}

    override fun init(filterConfig: FilterConfig?) {
        log.info { "xssFilter initialized" }
    }

    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        log.info { "xssFilter exec" }
        filterChain.doFilter(XssHttpServletRequestWrapper(servletRequest as HttpServletRequest?), servletResponse)
    }

    override fun destroy() {
        log.info { "xssFilter destroy" }
    }
}