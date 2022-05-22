package com.github.jchanghong.autoconfig.db.mybatis

import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import com.baomidou.mybatisplus.core.toolkit.Assert
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.ibatis.type.*
import org.postgresql.util.PGobject
import org.slf4j.LoggerFactory
import java.io.IOException
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

//PGUUIDTypeHandler
/**
 *JSONObject 和pg中的jsonb类型 if (jdbcType!=JdbcType.OTHER)return
 * @author : jiangchanghong
 *
 * @version : 2020-01-08 10:14
 **/
@MappedJdbcTypes(value = [JdbcType.OTHER])
@MappedTypes(value = [JSONObject::class])
@Alias("PGJsonTypeHandler")
open class PGJsonTypeHandler : BaseTypeHandler<JSONObject?>() {
    private val type = PGobject()

    init {
        type.type = "jsonb"
    }

    @Throws(SQLException::class)
    override fun setNonNullParameter(
        ps: PreparedStatement,
        i: Int,
        parameter: JSONObject?,
        jdbcType: JdbcType
    ) {
        type.value = parameter!!.toString()
        ps.setObject(i, type)
    }

    @Throws(SQLException::class)
    override fun getNullableResult(rs: ResultSet, columnName: String): JSONObject? {
        val string = rs.getString(columnName) ?: return null
        return JSONUtil.parseObj(string)
    }

    @Throws(SQLException::class)
    override fun getNullableResult(rs: ResultSet, columnIndex: Int): JSONObject? {
        val string = rs.getString(columnIndex) ?: return null
        return JSONUtil.parseObj(string)
    }

    @Throws(SQLException::class)
    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): JSONObject? {
        val string = cs.getString(columnIndex) ?: return null
        return JSONUtil.parseObj(string)
    }
}

@MappedTypes(Any::class)
@MappedJdbcTypes(value = [JdbcType.VARCHAR, JdbcType.OTHER])
@Alias("PGJacksonTypeHandler")
class PGJacksonTypeHandler(type: Class<*>) : AbstractJsonTypeHandler<Any?>() {
    private val type: Class<*>
    override fun parse(json: String?): Any? {
        this ?: return null
        return try {
            objectMapper.readValue(json, type)
        } catch (var3: IOException) {
            throw RuntimeException(var3)
        }
    }

    override fun toJson(obj: Any?): String? {
        this ?: return null
        return try {
            objectMapper.writeValueAsString(obj)
        } catch (var3: JsonProcessingException) {
            throw RuntimeException(var3)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(PGJacksonTypeHandler::class.java)
        private var objectMapper = jacksonObjectMapper()
    }

    init {
        if (log.isTraceEnabled) {
            log.trace("PGJacksonTypeHandler($type)")
        }
        Assert.notNull(type, "Type argument cannot be null", *arrayOfNulls(0))
        this.type = type
    }
}

/**
 *JSONObject 和pg中的jsonb类型 if (jdbcType!=JdbcType.OTHER)return
 * @author : jiangchanghong
 *
 * @version : 2020-01-08 10:14
 **/
@MappedJdbcTypes(value = [JdbcType.OTHER])
@MappedTypes(value = [String::class])
@Alias("PGUUIDTypeHandler")
open class PGUUIDTypeHandler : BaseTypeHandler<String?>() {
    private val type = PGobject()

    init {
        type.type = "uuid"
    }

    @Throws(SQLException::class)
    override fun setNonNullParameter(
        ps: PreparedStatement,
        i: Int,
        parameter: String?,
        jdbcType: JdbcType
    ) {
        type.value = parameter
        ps.setObject(i, type)
    }

    @Throws(SQLException::class)
    override fun getNullableResult(rs: ResultSet, columnName: String): String? {
        val string = rs.getString(columnName) ?: return null
        return string
    }

    @Throws(SQLException::class)
    override fun getNullableResult(rs: ResultSet, columnIndex: Int): String? {
        val string = rs.getString(columnIndex) ?: return null
        return string
    }

    @Throws(SQLException::class)
    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): String? {
        val string = cs.getString(columnIndex) ?: return null
        return string
    }
}
