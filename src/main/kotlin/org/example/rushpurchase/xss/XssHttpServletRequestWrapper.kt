package org.example.rushpurchase.xss

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import org.example.rushpurchase.utils.XssUtils
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.util.stream.Collectors

class XssHttpServletRequestWrapper(request: HttpServletRequest?) : HttpServletRequestWrapper(request) {

    override fun getParameterValues(name: String?): Array<String> {
        return super.getParameterValues(name).mapNotNull {
            filterParamString(it)
        }.toTypedArray()
    }

    override fun getParameterMap(): MutableMap<String, Array<String?>> {
        val result: MutableMap<String, Array<String?>> = HashMap()
        for ((key, value) in super.getParameterMap()) {
            result[key] = filterEntryString(value!!)
        }
        return result
    }

    override fun getParameter(name: String?): String? {
        return filterParamString(super.getParameter(name))
    }

    override fun getHeader(name: String?): String? {
        return filterParamString(super.getHeader(name))
    }

    override fun getCookies(): Array<Cookie> {
        val cookies = super.getCookies()
        cookies.let {
            for (cookie in cookies) {
                cookie.value = filterParamString(cookie.value)
            }
        }
        return cookies
    }

    override fun getInputStream(): ServletInputStream {
        val strContent = BufferedReader(InputStreamReader(super.getInputStream()))
            .lines()
            .collect(Collectors.joining(System.lineSeparator()))
        val toJSON = JSON.toJSON(strContent)
        var result: String? = null
        if (toJSON is JSONObject) {
            result = jsonObjectCleanXSS(toJSON).toJSONString()
        } else if (toJSON is JSONArray) {
            val map = toJSON.map {
                jsonObjectCleanXSS(it as JSONObject)
            }
            result = JSONObject.toJSONString(map)
        }
        val bAis = ByteArrayInputStream(result!!.toByteArray()) //再封装数据

        return object : ServletInputStream() {
            override fun isFinished(): Boolean {
                return false
            }

            override fun isReady(): Boolean {
                return false
            }

            override fun setReadListener(readListener: ReadListener) {
            }

            override fun read(): Int {
                return bAis.read()
            }
        }
    }

    private fun jsonObjectCleanXSS(jsonObject: JSONObject): JSONObject {
        val resultObj = JSONObject()
        jsonObject.entries.forEach { mutableEntry ->
            val value = mutableEntry.value
            val key = mutableEntry.key
            when (value) {
                is JSONArray -> {
                    val map = value.map {
                        jsonObjectCleanXSS(it as JSONObject)
                    }
                    resultObj[key] = map
                }

                is JSONObject -> {
                    jsonObjectCleanXSS(value)
                }

                else -> {
                    resultObj[key] = filterParamString(value as String)
                }
            }
        }
        return resultObj
    }

    private fun filterEntryString(value: Array<String?>): Array<String?> {
        for (i in value.indices) {
            value[i] = filterParamString(value[i])
        }
        return value
    }

    private fun filterParamString(value: String?): String? {
        return value?.let {
            return XssUtils.stripXSS(value)
        }
    }
}