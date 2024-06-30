package org.example.rushpurchase.utils

import com.alibaba.fastjson2.JSONObject
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.rushpurchase.xss.XssHttpServletRequestWrapper
import org.springframework.http.MediaType
import java.nio.charset.StandardCharsets

object RequestResponseUtil {

    fun getRequestParameter(servletRequest: HttpServletRequest): MutableMap<String, String> {
        val dataMap: MutableMap<String, String> = HashMap()
        servletRequest.parameterNames.iterator().forEach {
            val paraValue: String = getRequest(servletRequest).getParameter(it)
            dataMap[it] = paraValue
        }
        return dataMap
    }

    fun getRequestHeader(servletRequest: HttpServletRequest): MutableMap<String, String> {
        val dataMap: MutableMap<String, String> = HashMap()
        servletRequest.headerNames.iterator().forEach {
            val paraValue: String = getRequest(servletRequest).getHeader(it)
            dataMap[it] = paraValue
        }
        return dataMap
    }

    fun getRequestBody(servletRequest: HttpServletRequest): MutableMap<String, Any> {
        val dataMap: MutableMap<String, Any> = HashMap()

        if (servletRequest.getAttribute("body") != null) {
            val map = JSONObject.parseObject(servletRequest.getAttribute("body").toString()).toMap()
            dataMap.putAll(map)
        } else {
            val map = servletRequest.inputStream.bufferedReader().use { reader ->
                val readText = reader.readText()
                JSONObject.parseObject(readText).toMap()
            }
            dataMap.putAll(map)
            servletRequest.setAttribute("body", dataMap)
        }
        return dataMap
    }

    fun getParameter(servletRequest: HttpServletRequest, key: String): Any? =
        getRequest(servletRequest).getParameter(key)

    fun getHeader(servletRequest: HttpServletRequest, key: String): String? =
        getRequest(servletRequest).getHeader(key)

    private fun getRequest(servletRequest: HttpServletRequest): HttpServletRequest =
        XssHttpServletRequestWrapper(servletRequest)

    fun responseWrite(servletResponse: HttpServletResponse, outStr: String) {
        servletResponse.characterEncoding = StandardCharsets.UTF_8.name()
        servletResponse.contentType = MediaType.APPLICATION_JSON_VALUE
        servletResponse.writer.use {
            it.write(outStr)
        }
    }

}