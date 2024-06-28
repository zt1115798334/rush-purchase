package org.example.rushpurchase.cache.service

import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import java.util.concurrent.TimeUnit

interface RedisService {

    fun getObject(key: String): Any?

    fun getJSONObject(key: String): JSONObject?

    fun getJSONArray(key: String): JSONArray?

    fun setObject(key: String, value: Any)

    fun setObject(key: String, value: Any, timeout: Long, unit: TimeUnit)

    fun setJSONObject(key: String, value: JSONObject)

    fun setJSONObject(key: String, value: JSONObject, timeout: Long, unit: TimeUnit)

    fun setJSONArray(key: String, value: JSONArray)

    fun setJSONArray(key: String, value: JSONArray, timeout: Long, unit: TimeUnit)

    fun delete(key: String): Boolean
}