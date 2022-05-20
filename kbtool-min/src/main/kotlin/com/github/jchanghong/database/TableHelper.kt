package com.github.jchanghong.database

import cn.hutool.core.date.DateUtil
import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.db.DbUtil
import cn.hutool.db.meta.Column
import cn.hutool.db.meta.JdbcType
import cn.hutool.db.meta.MetaUtil
import cn.hutool.db.meta.Table
import cn.hutool.json.JSONUtil
import cn.hutool.system.SystemUtil
import com.github.jchanghong.str.toCamelCase
import org.slf4j.LoggerFactory
import javax.sql.DataSource

/** 数据库对应的java类型，常用*/
enum class JavaTypeName {
	String,
	Boolean,
	Long,
	Double,
	LocalDate,
	LocalDateTime,
	ByteArray,
	JSONObject
}


fun main() {
	println(StrUtil.upperFirst("aInt"))
	println("aInt".uppercase())

}

/**
 * 数据库表生成代码，生成sql等
 * @author : jiangchanghong
 * @version : 2020-01-02 17:38
 */
object TableHelper {
	lateinit var datasource: DataSource
	lateinit var table: Table
	private fun Column.toJsonHandler(): String {
		val jsonHandler =
			if (this.typeEnum.toJavaType(
					this
				) == JavaTypeName.JSONObject.name
			) ",jdbcType = OTHER,typeHandler = com.github.jchanghong.autoconfig.db.mybatis.PGJsonTypeHandler" else ""
		return jsonHandler
	}

	private fun JdbcType.toJavaType(column: Column): String {
		val s: JavaTypeName = when (this) {
			JdbcType.ARRAY -> JavaTypeName.String
			JdbcType.BIT -> JavaTypeName.Boolean
			JdbcType.TINYINT -> JavaTypeName.Long
			JdbcType.SMALLINT -> JavaTypeName.Long
			JdbcType.INTEGER -> JavaTypeName.Long
			JdbcType.BIGINT -> JavaTypeName.Long
			JdbcType.FLOAT -> JavaTypeName.Double
			JdbcType.REAL -> JavaTypeName.Double
			JdbcType.DOUBLE -> JavaTypeName.Double
			JdbcType.NUMERIC -> JavaTypeName.Double
			JdbcType.DECIMAL -> JavaTypeName.Double
			JdbcType.CHAR -> JavaTypeName.String
			JdbcType.VARCHAR -> JavaTypeName.String
			JdbcType.LONGVARCHAR -> JavaTypeName.String
			JdbcType.DATE -> JavaTypeName.LocalDate
			JdbcType.TIME -> JavaTypeName.LocalDateTime
			JdbcType.TIMESTAMP -> JavaTypeName.LocalDateTime
			JdbcType.BINARY -> JavaTypeName.ByteArray
			JdbcType.VARBINARY -> JavaTypeName.ByteArray
			JdbcType.LONGVARBINARY -> JavaTypeName.ByteArray
			JdbcType.NULL -> JavaTypeName.String
			JdbcType.BLOB -> JavaTypeName.ByteArray
			JdbcType.CLOB -> JavaTypeName.String
			JdbcType.BOOLEAN -> JavaTypeName.Boolean
			JdbcType.CURSOR -> JavaTypeName.String
			JdbcType.UNDEFINED -> JavaTypeName.String
			JdbcType.NVARCHAR -> JavaTypeName.String
			JdbcType.NCHAR -> JavaTypeName.String
			JdbcType.NCLOB -> JavaTypeName.String
			JdbcType.STRUCT -> JavaTypeName.String
			JdbcType.JAVA_OBJECT -> JavaTypeName.String
			JdbcType.DISTINCT -> JavaTypeName.String
			JdbcType.REF -> JavaTypeName.String
			JdbcType.DATALINK -> JavaTypeName.String
			JdbcType.ROWID -> JavaTypeName.Long
			JdbcType.LONGNVARCHAR -> JavaTypeName.String
			JdbcType.SQLXML -> JavaTypeName.String
			JdbcType.DATETIMEOFFSET -> JavaTypeName.String
			JdbcType.TIME_WITH_TIMEZONE -> JavaTypeName.LocalDateTime
			JdbcType.TIMESTAMP_WITH_TIMEZONE -> JavaTypeName.LocalDateTime
			JdbcType.OTHER -> {
				try {
					val query =
						DbUtil.use(datasource)
							.query("""select ${column.name} from ${table.tableName} limit 5""", String::class.java)
					val any = query.filterNotNull().any { !JSONUtil.isJson(it) }
					if (any) {
						JavaTypeName.String
					} else {
						JavaTypeName.JSONObject
					}
				} catch (e: Exception) {
					JavaTypeName.JSONObject
				}
			}
			else -> JavaTypeName.String
		}
		return s.name
	}


	private fun Table.toDOName(): String {
		return "DO" + StrUtil.upperFirst(StrUtil.toCamelCase(tableName))
	}

	private fun Table.toMapperName(): String {
		return "AutoMapper" + StrUtil.upperFirst(StrUtil.toCamelCase(tableName))
	}

	private fun Table.toServiceName(): String {
		return "AutoService" + StrUtil.upperFirst(StrUtil.toCamelCase(tableName))
	}

	private fun JdbcType.ToDefaultValue(column: Column): String =
		when (this.toJavaType(column)) {
			"Long", "Int,Double" -> "0"
			else -> ""
		}

	private val log = LoggerFactory.getLogger(TableHelper::class.java)!!
	private fun table(table: Table, packageName: String): String {
		return """
|package ${packageName}
            |import com.baomidou.mybatisplus.annotation.*
|import com.github.liaochong.myexcel.core.WorkbookType
|import com.github.liaochong.myexcel.core.annotation.ExcelColumn
|import com.github.liaochong.myexcel.core.annotation.ExcelTable
|import com.github.liaochong.myexcel.core.constant.FileType
|import com.github.liaochong.myexcel.core.constant.LinkType
|import org.apache.ibatis.type.JdbcType
|import org.apache.ibatis.type.UnknownTypeHandler
|import com.github.liaochong.myexcel.core.annotation.ExcelModel
|import java.time.LocalDate
|import java.time.LocalDateTime
            |import com.fasterxml.jackson.annotation.JsonFormat
            |import com.github.jchanghong.kotlin.*
            |import org.apache.ibatis.annotations.Update
|import org.springframework.stereotype.Service
|import com.baomidou.mybatisplus.core.mapper.BaseMapper
|import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
|import cn.hutool.json.JSONObject
|import com.github.jchanghong.autoconfig.db.mybatis.PGJsonTypeHandler

|/**
|${table.comment ?: ""}
 |* @Date ${DateUtil.today()}
 |* 此文件为自动生成，请勿修改！！！
 |* @User jiangchanghong
|*/
            |@ExcelModel(includeAllField = true,  excludeParent = false,titleSeparator = "->",useFieldNameAsTitle = true,  wrapText = true)
            |@TableName(value = "${table.tableName}", schema = "", keepGlobalPrefix = false, resultMap = "", autoResultMap = false)
            |data class DO${StrUtil.upperFirst(table.tableName.toCamelCase())} @JvmOverloads constructor"""
	}

	private fun oneColumn(
		column: Column,
		pk: Boolean,
		index: Int,
		strLength: Int = 0
	): String {
		val comment = if (StrUtil.isNotBlank(column.comment)) """
         |    /**${column.comment}*/
         """
		else "\n"
		val javaType = column.typeEnum.toJavaType(column)
		var width = if (javaType.startsWith("LocalDate")) 10 else -1
		if (strLength > 0) {
			width = strLength
		}
		val format = when (javaType) {
			"LocalDateTime" -> "yyyy-MM-dd HH:mm:ss"
			"LocalDate" -> "yyyy-MM-dd"
			else -> ""
		}
		val jacksonDate = when (javaType) {
			"LocalDateTime" -> """ |    @get:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            """
			"LocalDate" -> """  |    @get:JsonFormat(pattern = "yyyy-MM-dd")
            """
			else -> ""
		}
		val excel =
			"""|    @ExcelColumn(title = "${column.name.toCamelCase()}",order = ${index},  defaultValue = "${
				column.typeEnum.ToDefaultValue(
					column
				)
			}", convertToString = false,format = "$format",mapping = "", width = ${width})
        """
		val pkColumn = """|    @TableId(value = "${column.name}", type = IdType.AUTO)
        """
		val jsonHandler =
			if (javaType == JavaTypeName.JSONObject.name) ",jdbcType = JdbcType.OTHER,typeHandler = PGJsonTypeHandler::class" else ""
		val oThercolumn = """|    @TableField(value = "${column.name}",exist = true, numericScale = "" ${jsonHandler})
        """
		val prop = """|    var  ${column.name.toCamelCase()} : $javaType ? = null"""
		return comment + excel + (if (pk) pkColumn else oThercolumn) + jacksonDate + prop
	}

	private fun toInsertBatchPGSQL(): String {
		return """
             |interface ${table.toMapperName()} : BaseMapper<${table.toDOName()}>{
                |    @Update(""${'"'}
                    ${mybatisInsertBatchPGSQL(datasource, table)}
                ""${'"'})
                fun mybatisInsertBatchPG(list: Collection<${table.toDOName()}>)
            }
        """.trimIndent()
	}

	private fun autoService(): String {
		return """
            |@Service
             |open class ${table.toServiceName()}: ServiceImpl<${table.toMapperName()}, ${table.toDOName()}>()
        """
	}

	private fun toKotlin(packageName: String): String {
		val pkNames = table.pkNames
		return """
            ${table(table, packageName)}(${
			table.columns.withIndex().joinToString(",") {
				oneColumn(it.value, pkNames.contains(it.value.name), it.index)
			}
		}
       |)
       ${toInsertBatchPGSQL()}
       ${autoService()}
        """.trimMargin().trimIndent()
	}

	fun toFile(datasource: DataSource, table: String, packageName: String, subProject: String? = null): Unit {
		this.datasource = datasource
		this.table = MetaUtil.getTableMeta(datasource, table)
		val packageName1 = "$packageName.dao"
		var kotlin = toKotlin(packageName1)
		val separator = SystemUtil.get(SystemUtil.FILE_SEPARATOR, "/")
		var path =
			SystemUtil.getUserInfo().currentDir + separator + (if (subProject.isNullOrBlank()) "" else subProject + separator) + listOf(
				"src",
				"main",
				"java"
			)
				.joinToString(
					separator = separator,
					prefix = separator,
					postfix = separator
				) + separator + packageName1.split(".").joinToString(separator)
		path = path.replace(
			separator + separator,
			separator
		) + separator + """DO${StrUtil.upperFirst(table.toCamelCase())}""" + ".kt"
		FileUtil.touch(path)
		log.info(path)
		FileUtil.writeUtf8String(kotlin, path)
	}

	fun toFile(packageName: String, subProject: String? = null, datasource: DataSource) {
		var set = MetaUtil.getTables(datasource)
		val set2 = mutableSetOf<String>()
		for (name in set) {
			if (set2.contains(name)) continue
			var any =
				set2.any { it.replace("""_\d{1,2}""".toRegex(), "") == name.replace("""_\d{1,2}""".toRegex(), "") }
			if (any) continue
			set2.add(name)
		}
		for (s in set2) {
			println(s)
			toFile(datasource, s, packageName, subProject)
		}
	}

	/**
	 * 返回如下这样的,去掉关键字：
	INSERT INTO check2_camera_info_tmp_1 ( index_code,
	ok_record, ok_safe
	)
	VALUES
	<foreach collection="list" separator="," item="a">
	( #{a.indexCode}
	, #{a.okRecord}, #{a.okSafe}
	)
	</foreach>
	on conflict do nothing
	 * */
	fun mybatisInsertBatchPGSQL(datasource: DataSource, table: Table, removePK: Boolean = true): String {
		var pkNames = table.pkNames
		var columns = table.columns
		if (removePK) columns = columns.filterNot { pkNames.contains(it.name) }
		var dbnames = columns.map { it to it.name }
		var javaNames =
			dbnames.map { """#{a.${it.second.toCamelCase()} ${it.first.toJsonHandler()} }""" }
		return """
            <script>
             INSERT INTO ${table.tableName} ( ${dbnames.map { it.second }.joinToString(",")}
                )
                VALUES
                <foreach collection="list" separator="," item="a">
                ( ${javaNames.joinToString(",")}
                )
                </foreach>
                on conflict do nothing
                </script>
        """.trimIndent()
	}
}
