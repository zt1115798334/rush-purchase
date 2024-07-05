package org.example.rushpurchase.utils.excel

import java.util.function.BiFunction
import java.util.function.Function
import kotlin.reflect.full.memberProperties


object ExcelMapUtils {

    fun createExcelMap(any: Any, keys: Set<String>): Map<String, Any?> {
        val propertiesMap = any::class.memberProperties.associate {
            it.name to it.getter.call(any)
        }
        return propertiesMap.filterKeys(keys::contains)
    }

    fun createExcelMap(any: Any, keys: Set<String>, function: Function<Any, Any>): Map<String, Any?> {
        val propertiesMap = any::class.memberProperties.associate {
            it.name to it.getter.call(any)
        }
        return propertiesMap.filterKeys(keys::contains).mapValues { it.value?.let { value -> function.apply(value) } }
    }

    fun createExcelMap(any: Any, keys: Set<String>, function: BiFunction<String, Any, Any>): Map<String, Any?> {
        val propertiesMap = any::class.memberProperties.associate {
            it.name to it.getter.call(any)
        }
        return propertiesMap.filterKeys(keys::contains)
            .mapValues { it.value?.let { value -> function.apply(it.key, value) } }
    }
}
