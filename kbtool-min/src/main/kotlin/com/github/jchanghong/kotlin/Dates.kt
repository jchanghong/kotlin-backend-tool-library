package com.github.jchanghong.kotlin

import cn.hutool.core.date.DatePattern
import cn.hutool.core.date.DateUtil
import cn.hutool.core.date.LocalDateTimeUtil
import cn.hutool.core.date.format.FastDateFormat
import cn.hutool.core.util.ClassUtil
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

const val DATE_TIME_PATTERN = DatePattern.NORM_DATETIME_PATTERN
const val DATE_PATTERN = DatePattern.NORM_DATE_PATTERN

/**
\* Created with IntelliJ IDEA.
\* User: jiang
\* Date: 2020/1/5
\* Time: 11:02
\*/
fun LocalDateTime?.toStrOrNow(): String {
	this ?: return DateUtil.now()
	return LocalDateTimeUtil.formatNormal(this)
}

fun Date?.toStrOrNow(): String {
	this ?: return DateUtil.now()
	return DateUtil.formatDateTime(this)
}


fun LocalDate?.toStrOrNow(): String {
	this ?: return DateUtil.today()
	return LocalDateTimeUtil.formatNormal(this)
}

fun LocalDateTime?.toDateOrNow(): Date {
	this ?: return Date()
	return Date.from(this.toInstant(ZoneOffset.ofHours(8)))
}

fun LocalDate?.toDateOrNow(): Date {
	this ?: return Date()
	return Date.from(
		this.atTime(0, 0, 0)
			.toInstant(ZoneOffset.ofHours(8))
	)
}

fun Date?.toLocalDateTime(): LocalDateTime? {
	this ?: return null
	return LocalDateTime.parse(this.toStrOrNow(), DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
}

fun String?.toLocalDateTimeOrNow(): LocalDateTime {
	this ?: return LocalDateTime.now()
	return try {
		val dateTime: Date = this.toDateJdk7OrNull() ?: Date()
		dateTime.toLocalDateTime() ?: LocalDateTime.now()
	} catch (e: Exception) {
		LocalDateTime.now()
	}
}

fun String?.toLocalDateTimeOrNull(): LocalDateTime? {
	this ?: return null
	return try {
		val dateTime = this.toDateJdk7OrNull() ?: return null
		dateTime.toLocalDateTime()
	} catch (e: Exception) {
		e.printStackTrace()
		null
	}
}

fun String?.toLocalDateOrNull(): LocalDate? {
	this ?: return null
	return try {
		val dateTime = this.toDateJdk7OrNull() ?: return null
		return dateTime.toLocalDateTime()?.toLocalDate()
	} catch (e: Exception) {
		e.printStackTrace()
		null
	}
}

val gAllDatePatternList = ClassUtil.getDeclaredFields(DatePattern::class.java)
	.mapNotNull {
		try {
			val fastDateFormat = it.get(null) as? FastDateFormat?
			FastDateFormat.getInstance(fastDateFormat?.pattern, Locale.CHINA)
		} catch (e: Exception) {
			null
		}
	}
	.sortedByDescending { it.pattern.length }

fun String?.toDateJdk7OrNull(): Date? {
	if (this.isNullOrBlank()) return null
	runCatching { return DateUtil.parseDateTime(this).toJdkDate() }
	for (fastDateFormat in gAllDatePatternList) {
		runCatching { return fastDateFormat.parse(this) }
	}
	return null
}

fun main() {
	val toDateJdk7OrNull = "05:11:11".toDateJdk7OrNull()
	println(toDateJdk7OrNull)
	println(DateUtil.now().toLocalDateTimeOrNow().toStrOrNow())
	println(Date().toLocalDateTime().toStrOrNow())
	println(LocalDateTime.now().toStrOrNow())
	println(LocalDate.now().toStrOrNow())
	println(LocalDateTime.now().toDateOrNow().toStrOrNow())
	println(LocalDateTime.now().toLocalDate().toStrOrNow())

	println(LocalDateTimeUtil.of(Date()).toStrOrNow())

	println(LocalDateTime.now().toString().toLocalDateTimeOrNull()?.toStrOrNow())
	println(LocalDate.now().toString().toLocalDateTimeOrNull()?.toStrOrNow())
	println("01:01:01".toLocalDateTimeOrNull()?.toStrOrNow())
}
