package com.github.jchanghong.gson

import cn.hutool.core.date.DatePattern
import cn.hutool.json.JSONUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.jchanghong.kotlin.toDateJdk7OrNull
import com.github.jchanghong.log.kInfo
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


object JsonHelper {
	/** json to kotlin class ，对个json保证正确性*/
	fun jsonToKotlinClass(json: List<String>): String {
		return Json2kotlinclass.jsonToKotlinClass(json)
	}

	val objectMapper = newJacksonMapper()
	fun newJacksonMapper(): ObjectMapper {
		val objectMapper = jacksonObjectMapper()
		return configObjectMapper(objectMapper)
	}

	fun configObjectMapper(objectMapper: ObjectMapper): ObjectMapper {
		objectMapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true)
		objectMapper.configure(JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION, true)
		objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true)
		objectMapper.configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true)
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)

		objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS)
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
		objectMapper.dateFormat = SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN)
		val findModules = ObjectMapper.findModules()
		for (module in findModules) {
			if (module.javaClass.canonicalName.contains("JavaTimeModule")) continue
			objectMapper.registerModule(module)
			kInfo(module.javaClass.canonicalName + ":register")
		}
		val javaTimeModule = JavaTimeModule()
		javaTimeModule.addSerializer(
			LocalDateTime::class.java,
			LocalDateTimeSerializer(DatePattern.NORM_DATETIME_FORMATTER)
		)
		javaTimeModule.addSerializer(LocalDate::class.java, LocalDateSerializer(DatePattern.NORM_DATE_FORMATTER))
		javaTimeModule.addDeserializer(
			LocalDateTime::class.java,
			LocalDateTimeDeserializer2(DatePattern.NORM_DATETIME_FORMATTER)
		)
		javaTimeModule.addDeserializer(LocalDate::class.java, LocalDateDeserializer2(DatePattern.NORM_DATE_FORMATTER))


		objectMapper.registerModule(javaTimeModule)

		kInfo(javaTimeModule.javaClass.canonicalName + ":register")

		objectMapper.registerModule(JacksonModel2())

		return objectMapper
	}
}

class JacksonModel2 : SimpleModule() {
	init {
//        println("init")
		val value1 = object : StdDeserializer<Date>(Date::class.java) {
			override fun deserialize(parser: JsonParser?, ctxt: DeserializationContext?): Date? {
				parser ?: return null
				if (parser.currentToken == JsonToken.VALUE_NULL) return null
				if (parser.currentToken == JsonToken.VALUE_NUMBER_INT) return Date(parser.longValue)
				val toString = parser.valueAsString
				val toLocalDateTime = toString.toDateJdk7OrNull()
				return toLocalDateTime
			}
		}
		addDeserializer(Date::class.java, value1)
	}
}

fun main() {
	println(Date())
	println(LocalDateTime.now())
	val json =
		"""{"id":1,"name":111,"time1":"2020-09-12 18:00:27","date1":"2020-09-12 18:00:27","date2":"2020-09-12"}"""
	val json2 = """{"date2":1599840000000,"date1":"2020-09-12T18:02:56.320","time1":1599904827153,"name":111,"id":1}"""
	val json3 = """{"date2":1599840000000,"date1":1599904827156,"time1":1599904827156,"name":111,"id":1}"""
	val json4 =
		"""{"date2":1599840000000,"date1":"2020-09-12T18:02:56.320","time1":"2020-09-12T18:02:56.320","name":111,"id":1}"""
//    "2020-09-12 14:55:44"
	val toJsonToMap = TestJsonBigBean("sasa").toJsonToMap()
	val testBean2 = TestBean2(1, 111)
	val map = testBean2.toJsonToMap()!!
	val toJsonStr = JSONUtil.toJsonStr(testBean2)
	val toJsonStr1 = testBean2.toJsonStr()
	println(toJsonStr)
	println(toJsonStr1)
	val jsonToObject = toJsonStr.jsonToObject<TestBean2>()
	val jsonToObject1 = toJsonStr1.jsonToObject<TestBean2>()
	var jsonToObje3ct2 = json.jsonToObject<TestBean2>()
	val jsonToObje1ct2 = json2.jsonToObject<TestBean2>()
	val jsonToObject12 = json3.jsonToObject<TestBean2>()
	val jsonToObject21 = json4.jsonToObject<TestBean2>()
	println(jsonToObject)
}

internal data class TestJsonBigBean(var name: String, var list: List<TestBean2> = listOf(TestBean2(1, 111)))
