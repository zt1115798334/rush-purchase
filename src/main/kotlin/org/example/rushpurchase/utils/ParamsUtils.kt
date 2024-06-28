package org.example.rushpurchase.utils

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.multipart.MultipartFile

object ParamsUtils {

    fun parseParams(parameterNames: Array<String?>, parameterValues: Array<Any?>): String {
        val result = JSONArray()
        for (i in parameterNames.indices) {
            val parameterName = parameterNames[i]
            val parameterValueObj = parameterValues[i]

            val parameterValue = when {
                parameterValueObj is String -> parameterValueObj.toString()
                parameterValueObj is MultipartFile -> "MultipartFile"
                parameterValueObj is Array<*> && parameterValueObj.isArrayOf<MultipartFile>() -> "MultipartFile"
                parameterValueObj is Array<*> && JSON.toJSON(parameterValueObj) is JSONArray -> JSONArray.toJSONString(
                    parameterValueObj
                )
                parameterValueObj is HttpServletRequest || parameterValueObj is HttpServletResponse -> continue
                else -> JSONObject.toJSONString(parameterValueObj)
            }

            result.add(JSONObject.of(parameterName, parameterValue))
        }
        return result.toString()
    }


}