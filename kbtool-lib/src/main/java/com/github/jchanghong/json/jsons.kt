package com.github.jchanghong.json

import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import com.github.jchanghong.lang.exeTimeAvg
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

val logger = LoggerFactory.getLogger("jsons")

data class TestBean(
    var id: Long? = null,
    var name: String? = null,
    var time1: LocalDateTime = LocalDateTime.now(),
    var date1: Date = Date()
)

data class TestBean2(
    var id: Long? = null,
    var name: Long? = null,
    var time1: LocalDateTime = LocalDateTime.now(),
    var date1: Date = Date(),
    var date2: LocalDate = LocalDate.now()
)

fun main() {
    val bitList2 = (1..1).map { TestBean2(1, 123) }.toList()
    val bitList1 = (1..1).map { TestBean(1, "123") }.toList()
    println(exeTimeAvg(1) {
        val message1 = bitList1.toJsonStr()
        val toJsonToObject = bitList1.toJsonToObject<List<TestBean2>>()
        println(toJsonToObject == bitList2)
        println(message1)
        val jsonByPath2 = message1.jsonByPath("[1].time1")
        val jsonToObject = message1.jsonToObject<List<Map<String, String>>>()
        val jsonToObject2 = message1.jsonToObject<List<TestBean2>>()
        val jsonToObject3 = message1.jsonToObject<List<TestBean>>()

        val message2 = bitList2.toJsonStr()
        println(message2)
        val jsonByPath21 = message2.jsonByPath("[0].time1")
        val jsonToObject11 = message2.jsonToObject<List<Map<String, String>>>()
        val jsonToObject12 = message2.jsonToObject<List<TestBean2>>()
        val jsonToObject13 = message2.jsonToObject<List<TestBean>>()
        println(jsonByPath21!!.length)
    })
}


inline fun <reified T> String?.jsonToObject(): T? {
    this ?: return null
    return try {
        JsonHelper.objectMapper.readValue(this, object : com.fasterxml.jackson.core.type.TypeReference<T>() {})
    } catch (e: Exception) {
        logger.error(e.localizedMessage)
        null
    }
}

fun String?.jsonByPath(path: String): String? {
    this ?: return null
    return try {
        JsonHelper.objectMapper.readTree(this).findPath(path)
        val parse = JSONUtil.parse(this)
        return JSONUtil.getByPath(parse, path)?.toString()
    } catch (e: Exception) {
        logger.error(e.localizedMessage, e)
        null
    }
}

inline fun <reified T> Any?.toJsonToObject(): T? {
    this ?: return null
    return try {
        return JsonHelper.objectMapper.readValue(JsonHelper.objectMapper.writeValueAsString(this),
            object : com.fasterxml.jackson.core.type.TypeReference<T>() {})
    } catch (e: Exception) {
        logger.error(e.localizedMessage)
        null
    }
}

fun Any?.toJsonToMap(): JSONObject? {
    this ?: return null
    return try {
        val valueAsString = JsonHelper.objectMapper.writeValueAsString(this)
        return JSONUtil.parseObj(valueAsString)
    } catch (e: Exception) {
        logger.error(e.localizedMessage)
        null
    }
}

fun Any?.toJsonStr(): String? {
    this ?: return null
    return try {
        JsonHelper.objectMapper.writeValueAsString(this)
    } catch (e: Exception) {
        logger.error(e.localizedMessage, e)
        null
    }
}

fun String.toFormatJsonStr(): String {
    return JSONUtil.formatJsonStr(this)
}
