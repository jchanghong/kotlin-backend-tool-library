package com.github.jchanghong.json

import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.createType

internal object Json2kotlinclass {
    fun toType(list: List<Class<*>>): String {
        for (clazz in list) {
            if (clazz.typeParameters.size > 0) return "List<Any?>?"
        }
        try {
            val map = list.map {
                it.kotlin.allSuperclasses
                    .filter { !it.java.isInterface }
                    .map { it.createType(nullable = true) }
                    .toSet() + setOf(it.kotlin.createType(nullable = true))
            }
            val reduce = map.reduce { acc, set -> acc.intersect(set) }
            if (reduce.size == 1) return reduce.first().toString().removePrefix("kotlin.")
            val set = reduce - Any::class.createType(nullable = true)
            if (set.size == 1) return set.first().toString().removePrefix("kotlin.")
            val first = set.last()
            return first.toString().removePrefix("kotlin.")
        } catch (e: Exception) {
            throw e
        }
    }

    fun jsonToKotlinClass(json: List<String>): String {
        val map = json.map { it.jsonToObject<Map<String, Any?>>() }
        val allName = map.mapNotNull { it?.keys }.reduce { acc, set -> acc + set }

        val name2Type = allName.map { it to toType(map.mapNotNull { map -> map?.get(it)?.javaClass }) }.toMap()
        val joinToString = name2Type.entries.map { (k, v) ->
            "var $k :$v  =null"
        }.joinToString(separator = ",\n")

        return """
       data class C1($joinToString)
        """.trimIndent()
    }
}

fun main() {
    Json2kotlinclass.toType(listOf(""::class.java, Int::class.java, Double::class.java))
    Json2kotlinclass.toType(listOf(Int::class.java, Double::class.java))
    Json2kotlinclass.toType(listOf(Long::class.java, Long::class.java))
    val json = """
         {
        "devType": 1,
        "collectTime": 1598605222000,
        "msgType": 0,
        "devNo": "100086",
        "latitude": 0,
        "version": "1.0",
        "voltageLow": 0,
        "linkageDevCode": "",
        "siteNo": "",
     
        "rfidIdentifier": "01500234024416",
        "longitude": 0
    }
    """.trimIndent()

    val json2 = """
         {
        "devType": 1,
        "collectTime": 1598605222000,
        "msgType": 0,
        "devNo": "100086",
        "latitude": 0,
        "version": "1.0",
        "voltageLow": 0,
        "linkageDevCode": "",
        "siteNo": "",
    
        "rfidIdentifier": "01500234024416",
        "longitude": 0,
        "sub":$json
    }
    """.trimIndent()

    val json3 = """
         {
        "devType": 1,
        "collectTime": 1598605222000,
        "msgType": 0,
        "devNo": "100086",
        "latitude": 0.0,
        "version": "1.0",
        "voltageLow": 0,
        "linkageDevCode": "",
        "siteNo": "",
        
        "rfidIdentifier": "01500234024416",
        "longitude": 0,
        "sub":[$json]
    }
    """.trimIndent()

    val jsonToObject = json3.jsonToObject<Map<String, Any?>>()
    println(JsonHelper.jsonToKotlinClass(listOf(json, json3)))
}
