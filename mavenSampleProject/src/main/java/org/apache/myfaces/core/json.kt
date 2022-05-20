package org.apache.myfaces.core

import cn.hutool.core.date.DatePattern
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.github.jchanghong.gson.JsonHelper
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

@Component
class Config1 : Jackson2ObjectMapperBuilderCustomizer {
	override fun customize(jacksonObjectMapperBuilder: Jackson2ObjectMapperBuilder) {
		jacksonObjectMapperBuilder.dateFormat(SimpleDateFormat(DatePattern.NORM_DATETIME_MINUTE_PATTERN))
		jacksonObjectMapperBuilder.serializerByType(
			LocalDateTime::class.java,
			LocalDateTimeSerializer(DatePattern.NORM_DATETIME_FORMATTER)
		)
	}
}

@Component
class Jackson1(val objectMapper: ObjectMapper) : ApplicationRunner {

	override fun run(args: ApplicationArguments?) {
		val writeValueAsString = objectMapper.writeValueAsString(Date())
		println(writeValueAsString)
		println(objectMapper.writeValueAsString(LocalDateTime.now()))
		println(JsonHelper.objectMapper === objectMapper)
	}
}
