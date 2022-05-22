package com.github.jchanghong.json

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.JsonTokenId
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
import com.github.jchanghong.kotlin.*
import java.io.IOException
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Deserializer for Java 8 temporal [LocalDateTime]s.
 *
 * @author Nick Williams
 * @since 2.2.0
 */
class LocalDateTimeDeserializer2 : JSR310DateTimeDeserializerBase<LocalDateTime> {
    private constructor() : this(DEFAULT_FORMATTER) {}
    constructor(formatter: DateTimeFormatter?) : super(LocalDateTime::class.java, formatter) {}

    /**
     * Since 2.10
     */
    protected constructor(base: LocalDateTimeDeserializer2?, leniency: Boolean?) : super(base, leniency) {}

    override fun withDateFormat(formatter: DateTimeFormatter): LocalDateTimeDeserializer2 {
        return LocalDateTimeDeserializer2(formatter)
    }

    override fun withLeniency(leniency: Boolean): LocalDateTimeDeserializer2 {
        return LocalDateTimeDeserializer2(this, leniency)
    }

    override fun withShape(shape: JsonFormat.Shape): LocalDateTimeDeserializer2 {
        return this
    }

    @Throws(IOException::class)
    override fun deserialize(parser: JsonParser, context: DeserializationContext): LocalDateTime? {
        if (parser.currentToken == JsonToken.VALUE_NULL) return null
        if (parser.currentToken == JsonToken.VALUE_NUMBER_INT) return Date(parser.longValue).toLocalDateTime()
        val toString = parser.valueAsString
        val toLocalDateTime = toString.toLocalDateTimeOrNull()
        if (toLocalDateTime != null) return toLocalDateTime
        if (parser.hasTokenId(JsonTokenId.ID_STRING)) {
            val string = parser.text.trim { it <= ' ' }
            return if (string.isEmpty()) {
                if (!isLenient) {
                    _failForNotLenient(parser, context, JsonToken.VALUE_STRING)!!
                } else null
            } else try {
                if (_formatter == DEFAULT_FORMATTER) {
                    // JavaScript by default includes time and zone in JSON serialized Dates (UTC/ISO instant format).
                    if (string.length > 10 && string[10] == 'T') {
                        return if (string.endsWith("Z")) {
                            LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC)
                        } else {
                            LocalDateTime.parse(string, DEFAULT_FORMATTER)
                        }
                    }
                }
                LocalDateTime.parse(string, _formatter)
            } catch (e: DateTimeException) {
                _handleDateTimeException(context, e, string)
            }
        }
        if (parser.isExpectedStartArrayToken) {
            var t = parser.nextToken()
            if (t == JsonToken.END_ARRAY) {
                return null
            }
            if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT) &&
                context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
            ) {
                val parsed = deserialize(parser, context)
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(parser, context)
                }
                return parsed
            }
            if (t == JsonToken.VALUE_NUMBER_INT) {
                val result: LocalDateTime
                val year = parser.intValue
                val month = parser.nextIntValue(-1)
                val day = parser.nextIntValue(-1)
                val hour = parser.nextIntValue(-1)
                val minute = parser.nextIntValue(-1)
                t = parser.nextToken()
                if (t == JsonToken.END_ARRAY) {
                    result = LocalDateTime.of(year, month, day, hour, minute)
                } else {
                    val second = parser.intValue
                    t = parser.nextToken()
                    if (t == JsonToken.END_ARRAY) {
                        result = LocalDateTime.of(year, month, day, hour, minute, second)
                    } else {
                        var partialSecond = parser.intValue
                        if (partialSecond < 1000 &&
                            !context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                        ) partialSecond *= 1000000 // value is milliseconds, convert it to nanoseconds
                        if (parser.nextToken() != JsonToken.END_ARRAY) {
                            throw context.wrongTokenException(
                                parser, handledType(), JsonToken.END_ARRAY,
                                "Expected array to end"
                            )
                        }
                        result = LocalDateTime.of(year, month, day, hour, minute, second, partialSecond)
                    }
                }
                return result
            }
            context.reportInputMismatch<Any>(
                handledType(),
                "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT",
                t
            )
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return parser.embeddedObject as LocalDateTime
        }
        if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            _throwNoNumericTimestampNeedTimeZone(parser, context)
        }
        return _handleUnexpectedToken(context, parser, "Expected array or string.")
    }

    companion object {
        private const val serialVersionUID = 1L
        private val DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val INSTANCE = LocalDateTimeDeserializer2()
    }
}

class DateDeserializer2 : JSR310DateTimeDeserializerBase<Date> {
    private constructor() : this(DEFAULT_FORMATTER) {}
    constructor(formatter: DateTimeFormatter?) : super(Date::class.java, formatter) {}

    /**
     * Since 2.10
     */
    protected constructor(base: DateDeserializer2?, leniency: Boolean?) : super(base, leniency) {}

    override fun withDateFormat(formatter: DateTimeFormatter): DateDeserializer2 {
        return DateDeserializer2(formatter)
    }

    override fun withLeniency(leniency: Boolean): DateDeserializer2 {
        return DateDeserializer2(this, leniency)
    }

    override fun withShape(shape: JsonFormat.Shape): DateDeserializer2 {
        return this
    }

    @Throws(IOException::class)
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Date? {
        if (parser.currentToken == JsonToken.VALUE_NULL) return null
        if (parser.currentToken == JsonToken.VALUE_NUMBER_INT) return Date(parser.longValue)
        val toString = parser.valueAsString
        val toLocalDateTime = toString.toDateJdk7OrNull()
        if (toLocalDateTime != null) return toLocalDateTime
        if (parser.hasTokenId(JsonTokenId.ID_STRING)) {
            val string = parser.text.trim { it <= ' ' }
            return if (string.isEmpty()) {
                if (!isLenient) {
                    _failForNotLenient(parser, context, JsonToken.VALUE_STRING)!!
                } else null
            } else try {
                if (_formatter == DEFAULT_FORMATTER) {
                    // JavaScript by default includes time and zone in JSON serialized Dates (UTC/ISO instant format).
                    if (string.length > 10 && string[10] == 'T') {
                        return if (string.endsWith("Z")) {
                            LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC)?.toDateOrNow()
                        } else {
                            LocalDateTime.parse(string, DEFAULT_FORMATTER)?.toDateOrNow()
                        }
                    }
                }
                LocalDateTime.parse(string, _formatter)?.toDateOrNow()
            } catch (e: DateTimeException) {
                _handleDateTimeException(context, e, string)
            }
        }
        if (parser.isExpectedStartArrayToken) {
            var t = parser.nextToken()
            if (t == JsonToken.END_ARRAY) {
                return null
            }
            if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT) &&
                context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
            ) {
                val parsed = deserialize(parser, context)
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(parser, context)
                }
                return parsed
            }
            if (t == JsonToken.VALUE_NUMBER_INT) {
                val result: LocalDateTime
                val year = parser.intValue
                val month = parser.nextIntValue(-1)
                val day = parser.nextIntValue(-1)
                val hour = parser.nextIntValue(-1)
                val minute = parser.nextIntValue(-1)
                t = parser.nextToken()
                if (t == JsonToken.END_ARRAY) {
                    result = LocalDateTime.of(year, month, day, hour, minute)
                } else {
                    val second = parser.intValue
                    t = parser.nextToken()
                    if (t == JsonToken.END_ARRAY) {
                        result = LocalDateTime.of(year, month, day, hour, minute, second)
                    } else {
                        var partialSecond = parser.intValue
                        if (partialSecond < 1000 &&
                            !context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                        ) partialSecond *= 1000000 // value is milliseconds, convert it to nanoseconds
                        if (parser.nextToken() != JsonToken.END_ARRAY) {
                            throw context.wrongTokenException(
                                parser, handledType(), JsonToken.END_ARRAY,
                                "Expected array to end"
                            )
                        }
                        result = LocalDateTime.of(year, month, day, hour, minute, second, partialSecond)
                    }
                }
                return result?.toDateOrNow()
            }
            context.reportInputMismatch<Any>(
                handledType(),
                "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT",
                t
            )
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (parser.embeddedObject as? LocalDateTime)?.toDateOrNow()
        }
        if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            _throwNoNumericTimestampNeedTimeZone(parser, context)
        }
        return _handleUnexpectedToken(context, parser, "Expected array or string.")
    }

    companion object {
        private const val serialVersionUID = 1L
        private val DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val INSTANCE = DateDeserializer2()
    }
}

/**
 * Deserializer for Java 8 temporal [LocalDate]s.
 *
 * @author Nick Williams
 */
class LocalDateDeserializer2 : JSR310DateTimeDeserializerBase<LocalDate> {
    protected constructor() : this(DEFAULT_FORMATTER) {}
    constructor(dtf: DateTimeFormatter?) : super(LocalDate::class.java, dtf) {}

    /**
     * Since 2.10
     */
    constructor(base: LocalDateDeserializer2?, dtf: DateTimeFormatter?) : super(base, dtf) {}

    /**
     * Since 2.10
     */
    protected constructor(base: LocalDateDeserializer2?, leniency: Boolean?) : super(base, leniency) {}

    /**
     * Since 2.11
     */
    protected constructor(base: LocalDateDeserializer2?, shape: JsonFormat.Shape?) : super(base, shape) {}

    override fun withDateFormat(dtf: DateTimeFormatter): LocalDateDeserializer2 {
        return LocalDateDeserializer2(this, dtf)
    }

    override fun withLeniency(leniency: Boolean): LocalDateDeserializer2 {
        return LocalDateDeserializer2(this, leniency)
    }

    override fun withShape(shape: JsonFormat.Shape): LocalDateDeserializer2 {
        return LocalDateDeserializer2(this, shape)
    }

    @Throws(IOException::class)
    override fun deserialize(parser: JsonParser, context: DeserializationContext): LocalDate? {
        if (parser.currentToken == JsonToken.VALUE_NULL) return null
        if (parser.currentToken == JsonToken.VALUE_NUMBER_INT) return Date(parser.longValue).toLocalDateTime()
            ?.toLocalDate()
        val toString = parser.valueAsString
        val toLocalDateTime = toString.toLocalDateOrNull()
        if (toLocalDateTime != null) return toLocalDateTime
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            val string = parser.text.trim { it <= ' ' }
            if (string.length == 0) {
                return if (!isLenient) {
                    _failForNotLenient(parser, context, JsonToken.VALUE_STRING)!!
                } else null
            }
            // as per [datatype-jsr310#37], only check for optional (and, incorrect...) time marker 'T'
            // if we are using default formatter
            val format = _formatter
            return try {
                if (format == DEFAULT_FORMATTER) {
                    // JavaScript by default includes time in JSON serialized Dates (UTC/ISO instant format).
                    if (string.length > 10 && string[10] == 'T') {
                        return if (string.endsWith("Z")) {
                            LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC).toLocalDate()
                        } else {
                            LocalDate.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        }
                    }
                }
                LocalDate.parse(string, format)
            } catch (e: DateTimeException) {
                _handleDateTimeException(context, e, string)
            }
        }
        if (parser.isExpectedStartArrayToken) {
            val t = parser.nextToken()
            if (t == JsonToken.END_ARRAY) {
                return null
            }
            if (context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS) &&
                (t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT)
            ) {
                val parsed = deserialize(parser, context)
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(parser, context)
                }
                return parsed
            }
            if (t == JsonToken.VALUE_NUMBER_INT) {
                val year = parser.intValue
                val month = parser.nextIntValue(-1)
                val day = parser.nextIntValue(-1)
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    throw context.wrongTokenException(
                        parser, handledType(), JsonToken.END_ARRAY,
                        "Expected array to end"
                    )
                }
                return LocalDate.of(year, month, day)
            }
            context.reportInputMismatch<Any>(
                handledType(),
                "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT",
                t
            )
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return parser.embeddedObject as LocalDate
        }
        // 06-Jan-2018, tatu: Is this actually safe? Do users expect such coercion?
        return if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            // issue 58 - also check for NUMBER_INT, which needs to be specified when serializing.
            if (_shape == JsonFormat.Shape.NUMBER_INT || isLenient) {
                LocalDate.ofEpochDay(parser.longValue)
            } else _failForNotLenient(parser, context, JsonToken.VALUE_STRING)!!
        } else _handleUnexpectedToken(context, parser, "Expected array or string.")
    }

    companion object {
        private const val serialVersionUID = 1L
        private val DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE
        val INSTANCE = LocalDateDeserializer2()
    }
}
