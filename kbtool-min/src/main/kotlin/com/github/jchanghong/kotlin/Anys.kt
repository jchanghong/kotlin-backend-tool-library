package com.github.jchanghong.kotlin

import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.util.NumberUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.json.JSONObject
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties

fun main() {

}

fun Any.beanToMap(): MutableMap<String, Any?> {
	return BeanUtil.beanToMap(this)
}

/** fill null bean类*/
inline fun <reified T : Any> T.beanFillNullValues(): T {
	val props = T::class.memberProperties
		.mapNotNull { it as? KMutableProperty<*> }
	for (property in props) {
		if (property.getter.call(this) != null) continue
		when (property.returnType) {
			LocalDateTime::class.createType(nullable = true) -> property.setter.call(this, LocalDateTime.now())
			LocalDate::class.createType(nullable = true) -> property.setter.call(this, LocalDate.now())
			Long::class.createType(nullable = true) -> property.setter.call(this, 0L)
			Boolean::class.createType(nullable = true) -> property.setter.call(this, false)
			Int::class.createType(nullable = true) -> property.setter.call(this, 0)
			Byte::class.createType(nullable = true) -> property.setter.call(this, 0.toByte())
			Double::class.createType(nullable = true) -> property.setter.call(this, 0.0)
			Float::class.createType(nullable = true) -> property.setter.call(this, 0.0.toFloat())
			String::class.createType(nullable = true) -> property.setter.call(this, StrUtil.EMPTY)
			Date::class.createType(nullable = true) -> property.setter.call(this, Date())
			Timestamp::class.createType(nullable = true) -> property.setter.call(this, Timestamp(Date().time))
			JSONObject::class.createType(nullable = true) -> property.setter.call(this, JSONObject())
			else -> {
			}
		}
	}
	return this
}

/** round 所有double flout*/
@JvmOverloads
inline fun <reified T : Any> T.beanRound(scale: Int = 2): T {
	val props = T::class.memberProperties
		.mapNotNull { it as? KMutableProperty<*> }
	for (property in props) {
		when (property.returnType) {
			Double::class.createType(nullable = true), Double::class.createType() -> {
				val value: Double? = property.getter.call(this) as? Double?
				if (value == null) {
					property.setter.call(this, NumberUtil.round(0.0, scale).toDouble())
				} else {
					property.setter.call(this, NumberUtil.round(value, scale).toDouble())
				}
			}
			Float::class.createType(nullable = true), Float::class.createType() -> {
				val value: Float? = property.getter.call(this) as? Float?

				if (value == null) {
					property.setter.call(this, NumberUtil.round(0.0, scale).toFloat())
				} else {
					property.setter.call(this, NumberUtil.round(value.toDouble(), scale).toFloat())
				}
			}
			else -> {
			}
		}
	}
	return this
}
