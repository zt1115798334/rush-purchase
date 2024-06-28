package org.example.rushpurchase.base

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import com.blazebit.persistence.PagedList
import org.example.rushpurchase.base.ResultMessage.Util
import org.springframework.data.domain.Page

open class BaseResultMessage {

    open fun success(): ResultMessage {
        return ResultMessage(meta = Util.successMeta())
    }

    open fun success(data: String): ResultMessage {
        return ResultMessage(meta = Util.successMeta(), value = data)
    }

    open fun success(data: Any): ResultMessage {
        return if (JSON.toJSON(data) is JSONObject) {
            success(JSON.toJSON(data) as JSONObject)
        } else {
            success(JSON.toJSON(data) as JSONArray)
        }
    }

    open fun success(data: JSONObject): ResultMessage {
        return ResultMessage(meta = Util.successMeta(), obj = data)
    }

    open fun success(data: JSONArray): ResultMessage {
        return ResultMessage(meta = Util.successMeta(), list = data)
    }

    open fun success(pageNumber: Int, pageSize: Int, total: Long, rows: Any): ResultMessage {
        return ResultMessage(meta = Util.successMeta(), page = Util.createPageData(pageNumber, pageSize, total, rows))
    }

    open fun success(scrollId: String, rows: Any): ResultMessage {
        return ResultMessage(meta = Util.successMeta(), scroll = Util.createScrollData(scrollId, rows))

    }

    open fun success(page: Page<*>): ResultMessage {
        return ResultMessage(
            meta = Util.successMeta(),
            page = Util.createPageData(page.number + 1, page.size, page.totalElements, page.content)
        )
    }

    open fun success(page: PagedList<*>): ResultMessage {
        return ResultMessage(
            meta = Util.successMeta(),
            page = Util.createPageData(page.page, page.size, page.totalSize, page)
        )
    }

    open fun failure(message: String): ResultMessage {
        return ResultMessage(meta = Util.errorMeta(message))
    }


}