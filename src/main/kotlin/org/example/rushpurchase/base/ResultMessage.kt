package org.example.rushpurchase.base

import cn.hutool.core.util.StrUtil
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import com.fasterxml.jackson.annotation.JsonInclude
import org.example.rushpurchase.enums.SystemStatusCode
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ResultMessage(
    val meta: Meta? = null,
    val page: PageData? = null,
    val scroll: ScrollData? = null,
    val obj: JSONObject? = null,
    val list: JSONArray? = null,
    val value: String? = null
) {
    data class Meta(val success: Boolean, val code: Int, val timestamp: LocalDateTime, val message: String)

    data class PageData(val pageNUmber: Int, val pageSize: Int, val total: Long, val list: JSONArray)

    data class ScrollData(val scrollId: String, val list: JSONArray)

    object Util {

        fun createPageData(pageNumber: Int, pageSize: Int, total: Long, rows: Any): PageData {
            val list: JSONArray = if (JSON.toJSON(rows) is JSONArray) {
                rows as JSONArray
            } else {
                JSONArray.of(rows)
            }
            return PageData(pageNumber, pageSize, total, list)
        }

        fun createScrollData(scrollId: String, rows: Any): ScrollData {
            val list: JSONArray = if (JSON.toJSON(rows) is JSONArray) {
                rows as JSONArray
            } else {
                JSONArray.of(rows)
            }
            return ScrollData(scrollId, list)
        }

        fun createData(obj: Any): ResultMessage {
            return if (JSON.toJSON(obj) is JSONObject) {
                ResultMessage(obj = obj as JSONObject)
            } else if (JSON.toJSON(obj) is JSONArray) {
                ResultMessage(list = obj as JSONArray)
            } else {
                ResultMessage(value = obj.toString())
            }
        }

        fun successMeta(): Meta = Meta(true, SystemStatusCode.SUCCESS.code, LocalDateTime.now(), StrUtil.EMPTY)

        fun successMeta(message: String): Meta = Meta(true, SystemStatusCode.SUCCESS.code, LocalDateTime.now(), message)

        fun successMeta(code: Int, message: String): Meta = Meta(true, code, LocalDateTime.now(), message)

        fun errorMeta(): Meta = Meta(false, SystemStatusCode.FAILURE.code, LocalDateTime.now(), StrUtil.EMPTY)

        fun errorMeta(message: String): Meta = Meta(false, SystemStatusCode.SUCCESS.code, LocalDateTime.now(), message)

        fun errorMeta(code: Int, message: String): Meta = Meta(false, code, LocalDateTime.now(), message)

        fun errorPage(): ResultMessage {
            val pageData = PageData(0, 0, 0, JSONArray.of())
            return ResultMessage(meta = errorMeta(), page = pageData)
        }


    }
}
