package org.example.rushpurchase.utils

import cn.hutool.http.HtmlUtil
import java.util.regex.Pattern

object XssUtils {

    fun stripXSS(value: String?): String? {
        var rlt: String?


        // Avoid null characters
        rlt = value!!.replace("".toRegex(), "")
        // Avoid anything between script tags
        var scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE)
        rlt = rlt.let { scriptPattern.matcher(it).replaceAll("") }


        // Remove any lonesome </script> tag
        scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE)
        rlt = rlt?.let { scriptPattern.matcher(it).replaceAll("") }


        // Remove any lonesome <script ...> tag
        scriptPattern = Pattern.compile(
            "<script(.*?)>", Pattern.CASE_INSENSITIVE
                    or Pattern.MULTILINE or Pattern.DOTALL
        )
        rlt = rlt?.let { scriptPattern.matcher(it).replaceAll("") }


        // Avoid eval(...) expressions
        scriptPattern = Pattern.compile(
            "eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE
                    or Pattern.MULTILINE or Pattern.DOTALL
        )
        rlt = rlt?.let { scriptPattern.matcher(it).replaceAll("") }


        // Avoid expression(...) expressions
        scriptPattern = Pattern.compile(
            "expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE
                    or Pattern.MULTILINE or Pattern.DOTALL
        )
        rlt = rlt?.let { scriptPattern.matcher(it).replaceAll("") }


        // Avoid javascript:... expressions
        scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE)
        rlt = rlt?.let { scriptPattern.matcher(it).replaceAll("") }


        // Avoid vbscript:... expressions
        scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE)
        rlt = rlt?.let { scriptPattern.matcher(it).replaceAll("") }


        // Avoid onload= expressions
        scriptPattern = Pattern.compile(
            "onload(.*?)=", Pattern.CASE_INSENSITIVE
                    or Pattern.MULTILINE or Pattern.DOTALL
        )
        rlt = rlt?.let { scriptPattern.matcher(it).replaceAll("") }


        // Avoid onerror= expressions
        scriptPattern = Pattern.compile(
            "onerror(.*?)=", Pattern.CASE_INSENSITIVE
                    or Pattern.MULTILINE or Pattern.DOTALL
        )
        rlt = rlt?.let { scriptPattern.matcher(it).replaceAll("") }

        //html转义
        rlt = HtmlUtil.unescape(rlt)
        return rlt
    }

    private fun stripSqlInjection(value: String?): String {
        val vle = value!!.replace("|", "")
        val badStr = """
            '|exec|execute|insert|select|delete|update|count|drop|%|chr|mid|master|truncate|
            table|from|grant|use|group_concat|column_name|
            information_schema.columns|table_schema|union|where|select|delete|update|order|by|count|
            chr|mid|master|truncate|char|declare|;|-|--|,|like|//|/|%|#| 
        """.trimIndent()

        val values = vle.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val badStrArr = badStr.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (aBadStrArr in badStrArr) {
            for (j in values.indices) {
                if (values[j].equals(aBadStrArr, ignoreCase = true)) {
                    values[j] = "forbid"
                    values[j] = ""
                }
            }
        }
        val sb = StringBuilder()
        for (i in values.indices) {
            if (i == values.size - 1) {
                sb.append(values[i])
            } else {
                sb.append(values[i]).append(" ")
            }
        }
        return sb.toString()
    }

    fun stripSqlXss(value: String): String? {
        return stripXSS(stripSqlInjection(value))
    }
}