@file:JvmName("zidongshengc")

package com.github.jchanghong.database

import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.db.DbUtil
import cn.hutool.db.meta.Column
import cn.hutool.db.meta.JdbcType
import cn.hutool.db.meta.JdbcType.*
import cn.hutool.db.meta.MetaUtil
import cn.hutool.db.meta.Table
import cn.hutool.extra.template.engine.freemarker.FreemarkerEngine
import cn.hutool.json.JSONArray
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import cn.hutool.system.SystemUtil
import com.github.jchanghong.str.toCamelCase
import com.github.jchanghong.str.upperFirst
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import java.io.File
import java.math.BigDecimal
import java.time.*
import java.util.*
import javax.sql.DataSource
import kotlin.io.path.Path

class JpaCodeHelperSetting {
    var overrideEntity = true
    var overrideEntityvo = false
    var overrideEntityexcel = false
    var overriderepository = false
    var overrideserive = false
    var overrideserviceImpl = false
    var overrideserviceImplTest = false

    /** 所有类后缀*/
    var allClassNameSuffix = ""

    /** 生成文件的默认位置 /src*/
    var fileRootPath = "/src"
    var onlyEntityAndRepository = true

    companion object {
        fun default(): JpaCodeHelperSetting {
            return JpaCodeHelperSetting()
        }
    }
}

/** 自动生成jpa相关代码和配置*/
object JpaCodeHelper {
    val freemarkerEngine = FreemarkerEngine()

    @JvmOverloads
    @Synchronized
    fun createCode(
        ds: DataSource,
        jpaCodeHelperSetting: JpaCodeHelperSetting,
        packageName: String,
        tableName: String? = null
    ): Unit {
        val tables = if (tableName.isNullOrBlank()) MetaUtil.getTables(ds) else listOf(tableName)
        tables.forEach { createCode2(ds, it, packageName, jpaCodeHelperSetting) }
    }

    private fun createCode2(
        ds: DataSource,
        tableName: String,
        packageName: String,
        jpaCodeHelperSetting: JpaCodeHelperSetting
    ) {
        val dbTable = DBTable(MetaUtil.getTableMeta(ds, tableName), packageName, jpaCodeHelperSetting)
        val entityTemplate = ClassPathResource("ftls/entity.ftl").inputStream.bufferedReader().readText()
        val repositoryTemplate = ClassPathResource("ftls/repository.ftl").inputStream.bufferedReader().readText()
        val serviceTemplate = ClassPathResource("ftls/service.ftl").inputStream.bufferedReader().readText()
        val serviceImplTemplate = ClassPathResource("ftls/serviceimpl.ftl").inputStream.bufferedReader().readText()
        val entityVoTemplate = ClassPathResource("ftls/entityvo.ftl").inputStream.bufferedReader().readText()
        val entityExcelTemplate = ClassPathResource("ftls/entityexcel.ftl").inputStream.bufferedReader().readText()
        val entitytestTemplate = ClassPathResource("ftls/test.ftl").inputStream.bufferedReader().readText()
        entityName(entityTemplate, ds, tableName, dbTable, jpaCodeHelperSetting)
        /** spring data jpa接口名*/
        repositoryName(repositoryTemplate, ds, tableName, dbTable, jpaCodeHelperSetting)
        if (jpaCodeHelperSetting.onlyEntityAndRepository) return
        serviceName(serviceTemplate, ds, tableName, dbTable, jpaCodeHelperSetting)
        serviceImplName(serviceImplTemplate, ds, tableName, dbTable, jpaCodeHelperSetting)
        entityVoName(entityVoTemplate, ds, tableName, dbTable, jpaCodeHelperSetting)
        entityExcelName(entityExcelTemplate, ds, tableName, dbTable, jpaCodeHelperSetting)
        entitytestTemplate(entitytestTemplate, ds, tableName, dbTable, jpaCodeHelperSetting)
        createDataSourceConfiguration(
            ClassPathResource("ftls/DataSourceConfiguration.ftl").inputStream.bufferedReader().readText(),
            dbTable,
            jpaCodeHelperSetting
        )
        createJpaFirstConfiguration(
            ClassPathResource("ftls/JpaFirstConfiguration.ftl").inputStream.bufferedReader().readText(),
            dbTable,
            jpaCodeHelperSetting
        )
        createJpaSecondConfiguration(
            ClassPathResource("ftls/JpaSecondConfiguration.ftl").inputStream.bufferedReader().readText(),
            dbTable,
            jpaCodeHelperSetting
        )
        createJpaThirdConfiguration(
            ClassPathResource("ftls/JpaThirdConfiguration.ftl").inputStream.bufferedReader().readText(),
            dbTable,
            jpaCodeHelperSetting
        )
    }

    private fun createJpaSecondConfiguration(
        readText: String,
        dbTable: DBTable,
        jpaCodeHelperSetting: JpaCodeHelperSetting
    ) {
        val template = freemarkerEngine.getTemplate(readText)
        val map = JSONUtil.parseObj(JSONUtil.toJsonStr(dbTable)).toMap()
        val basepackagePath = "/" + StrUtil.splitTrim(dbTable.packageName, ".").joinToString("/")
        val file = Path(
            SystemUtil.getUserInfo().currentDir,
            "${jpaCodeHelperSetting.fileRootPath}/main/java",
            basepackagePath,
            "/JpaSecondConfiguration.java"
        ).toFile()
        if (file.exists()) {
            return
        }
        template.render(map, file)
        logger.info("template.render:${file.absolutePath}")
    }

    private fun createJpaThirdConfiguration(
        readText: String,
        dbTable: DBTable,
        jpaCodeHelperSetting: JpaCodeHelperSetting
    ) {
        val template = freemarkerEngine.getTemplate(readText)
        val map = JSONUtil.parseObj(JSONUtil.toJsonStr(dbTable)).toMap()
        val basepackagePath = "/" + StrUtil.splitTrim(dbTable.packageName, ".").joinToString("/")
        val file = Path(
            SystemUtil.getUserInfo().currentDir,
            "${jpaCodeHelperSetting.fileRootPath}/main/java",
            basepackagePath,
            "/JpaThirdConfiguration.java"
        ).toFile()
        if (file.exists()) {
            return
        }
        template.render(map, file)
        logger.info("template.render:${file.absolutePath}")
    }

    private fun createJpaFirstConfiguration(
        readText: String,
        dbTable: DBTable,
        jpaCodeHelperSetting: JpaCodeHelperSetting
    ) {
        val template = freemarkerEngine.getTemplate(readText)
        val map = JSONUtil.parseObj(JSONUtil.toJsonStr(dbTable)).toMap()
        val basepackagePath = "/" + StrUtil.splitTrim(dbTable.packageName, ".").joinToString("/")
        val file = Path(
            SystemUtil.getUserInfo().currentDir,
            "${jpaCodeHelperSetting.fileRootPath}/main/java",
            basepackagePath,
            "/JpaFirstConfiguration.java"
        ).toFile()
        if (file.exists()) {
            return
        }
        template.render(map, file)
        logger.info("template.render:${file.absolutePath}")
    }

    private fun createDataSourceConfiguration(
        readText: String,
        dbTable: DBTable,
        jpaCodeHelperSetting: JpaCodeHelperSetting
    ) {
        val template = freemarkerEngine.getTemplate(readText)
        val map = JSONUtil.parseObj(JSONUtil.toJsonStr(dbTable)).toMap()
        val basepackagePath = "/" + StrUtil.splitTrim(dbTable.packageName, ".").joinToString("/")
        val file = Path(
            SystemUtil.getUserInfo().currentDir,
            "${jpaCodeHelperSetting.fileRootPath}/main/java",
            basepackagePath,
            "/DataSourceConfiguration.java"
        ).toFile()
        if (file.exists()) {
            return
        }
        template.render(map, file)
        logger.info("template.render:${file.absolutePath}")
    }

    private val logger = LoggerFactory.getLogger(JpaCodeHelper::class.java)
    private fun entitytestTemplate(
        entitytestTemplate: String,
        ds: DataSource,
        tableName: String,
        dbTable: DBTable,
        jpaCodeHelperSetting: JpaCodeHelperSetting
    ) {
        val template = freemarkerEngine.getTemplate(entitytestTemplate)
        val map = JSONUtil.parseObj(JSONUtil.toJsonStr(dbTable)).toMap()
        val file = File(dbTable.serviceImplTestNamePath)
        if (file.exists() && !jpaCodeHelperSetting.overrideserviceImplTest) {
            return
        }
        template.render(map, file)
        logger.info("template.render:${dbTable.serviceImplTestNamePath}")
    }

    private fun entityExcelName(
        entityExcelTemplate: String,
        ds: DataSource,
        tableName: String,
        dbTable: DBTable,
        jpaCodeHelperSetting: JpaCodeHelperSetting
    ) {
        val template = freemarkerEngine.getTemplate(entityExcelTemplate)
        val map = JSONUtil.parseObj(JSONUtil.toJsonStr(dbTable)).toMap()
        val file = File(dbTable.entityExcelNamePath)
        if (file.exists() && !jpaCodeHelperSetting.overrideEntityexcel) {
            return
        }
        template.render(map, file)
        logger.info("template.render:${dbTable.entityExcelNamePath}")

    }

    private fun entityVoName(
        entityVoTemplate: String,
        ds: DataSource,
        tableName: String,
        dbTable: DBTable,
        jpaCodeHelperSetting: JpaCodeHelperSetting
    ) {
        val template = freemarkerEngine.getTemplate(entityVoTemplate)
        val map = JSONUtil.parseObj(JSONUtil.toJsonStr(dbTable)).toMap()
        val file = File(dbTable.entityVoNamePath)
        if (file.exists() && !jpaCodeHelperSetting.overrideEntityvo) {
            return
        }
        template.render(map, file)
        logger.info("template.render:${dbTable.entityVoNamePath}")

    }

    private fun serviceImplName(
        serviceImplTemplate: String,
        ds: DataSource,
        tableName: String,
        dbTable: DBTable,
        jpaCodeHelperSetting: JpaCodeHelperSetting
    ) {
        val template = freemarkerEngine.getTemplate(serviceImplTemplate)
        val map = JSONUtil.parseObj(JSONUtil.toJsonStr(dbTable)).toMap()
        val file = File(dbTable.serviceImplNamePath)
        if (file.exists() && !jpaCodeHelperSetting.overrideserviceImpl) {
            return
        }
        template.render(map, file)
        logger.info("template.render:${dbTable.serviceImplName}")

    }

    private fun serviceName(
        serviceTemplate: String,
        ds: DataSource,
        tableName: String,
        dbTable: DBTable,
        jpaCodeHelperSetting: JpaCodeHelperSetting
    ) {
        val template = freemarkerEngine.getTemplate(serviceTemplate)
        val map = JSONUtil.parseObj(JSONUtil.toJsonStr(dbTable)).toMap()
        val file = File(dbTable.serviceNamePath)
        if (file.exists() && !jpaCodeHelperSetting.overrideserive) {
            return
        }
        template.render(map, file)
        logger.info("template.render:${dbTable.serviceName}")

    }

    private fun repositoryName(
        repositoryTemplate: String,
        ds: DataSource,
        tableName: String,
        dbTable: DBTable,
        jpaCodeHelperSetting: JpaCodeHelperSetting
    ) {
        val template = freemarkerEngine.getTemplate(repositoryTemplate)
        val map = JSONUtil.parseObj(JSONUtil.toJsonStr(dbTable)).toMap()
        val file = File(dbTable.repositoryNamePath)
        if (file.exists() && !jpaCodeHelperSetting.overriderepository) {
            return
        }
        template.render(map, file)
        logger.info("template.render:${dbTable.repositoryNamePath}")

    }

    private fun entityName(
        entityTemplate: String,
        ds: DataSource,
        tableName: String,
        dbTable: DBTable,
        jpaCodeHelperSetting: JpaCodeHelperSetting
    ) {
        val template = freemarkerEngine.getTemplate(entityTemplate)
        val map = JSONUtil.parseObj(JSONUtil.toJsonStr(dbTable)).toMap()
        val file = File(dbTable.entityNamePath)
        if (file.exists() && !jpaCodeHelperSetting.overrideEntity) {
            return
        }
        template.render(map, file)
        logger.info("template.render:${dbTable.entityNamePath}")

    }

}

class DBTable(val table: Table, val packageName: String, val jpaCodeHelperSetting: JpaCodeHelperSetting) {
    var columns: MutableList<DBColumn> = mutableListOf()
    var str_columns: MutableList<DBColumn> = mutableListOf()

    /** 主键*/
    var pk_columns: MutableList<DBColumn> = mutableListOf()
//    -------------class name
    /** 实体类名*/
    var entityName = ""

    /** spring data jpa接口名*/
    var repositoryName = ""
    var serviceName = ""
    var serviceImplName = ""
    var entityVoName = ""
    var entityExcelName = ""
    var serviceImplTestName = ""

    //    ---------------file path
    var entityNamePath = ""
    var repositoryNamePath = ""
    var serviceNamePath = ""
    var serviceImplNamePath = ""
    var entityVoNamePath = ""
    var entityExcelNamePath = ""
    var serviceImplTestNamePath = ""

    //    ---------------file package
    var entityNamePackage = ""
    var repositoryNamePackage = ""
    var serviceNamePackage = ""
    var serviceImplNamePackage = ""
    var entityVoNamePackage = ""
    var entityExcelNamePackage = ""
    var serviceImplTestNamePackage = ""
//    -------------other

    var firstAutoIncrementPrpertyName = ""
    var firstAutoIncrementPrpertyType = ""
    var today = ""

    /** 主键类型，可能是复合主键*/
    var pkJavaType = ""

    init {
        val tableName = table.tableName.toCamelCase().upperFirst()
        //    -------------class name
        /** 实体类名*/
        entityName = "Jpa${tableName}Entity${jpaCodeHelperSetting.allClassNameSuffix}".trim()
        /** spring data jpa接口名*/
        repositoryName = "Jpa${tableName}Repository${jpaCodeHelperSetting.allClassNameSuffix}".trim()
        serviceName = "Jpa${tableName}Service${jpaCodeHelperSetting.allClassNameSuffix}".trim()
        serviceImplName = "Jpa${tableName}ServiceImpl${jpaCodeHelperSetting.allClassNameSuffix}".trim()
        entityVoName = "Jpa${tableName}EntityVO${jpaCodeHelperSetting.allClassNameSuffix}".trim()
        entityExcelName = "Jpa${tableName}EntityExcel${jpaCodeHelperSetting.allClassNameSuffix}".trim()
        serviceImplTestName = "Jpa${tableName}ServiceImplTest${jpaCodeHelperSetting.allClassNameSuffix}".trim()
//    ---------------file path

        val currentDir = SystemUtil.getUserInfo().currentDir
        val basepackagePath = "/" + StrUtil.splitTrim(packageName, ".").joinToString("/")
        entityNamePath = Path(
            currentDir,
            "${jpaCodeHelperSetting.fileRootPath}/main/kotlin",
            basepackagePath,
            "/entity/${entityName}.kt"
        ).toFile().absolutePath
        repositoryNamePath = Path(
            currentDir,
            "${jpaCodeHelperSetting.fileRootPath}/main/kotlin",
            basepackagePath,
            "/repository/${repositoryName}.kt"
        ).toFile().absolutePath
        serviceNamePath = Path(
            currentDir,
            "${jpaCodeHelperSetting.fileRootPath}/main/kotlin",
            basepackagePath,
            "/service/${serviceName}.kt"
        ).toFile().absolutePath
        serviceImplNamePath = Path(
            currentDir,
            "${jpaCodeHelperSetting.fileRootPath}/main/kotlin",
            basepackagePath,
            "/service/impl/${serviceImplName}.kt"
        ).toFile().absolutePath
        entityVoNamePath = Path(
            currentDir,
            "${jpaCodeHelperSetting.fileRootPath}/main/kotlin",
            basepackagePath,
            "/entityvo/${entityVoName}.kt"
        ).toFile().absolutePath
        entityExcelNamePath = Path(
            currentDir,
            "${jpaCodeHelperSetting.fileRootPath}/main/kotlin",
            basepackagePath,
            "/entityexcel/${entityExcelName}.kt"
        ).toFile().absolutePath
        serviceImplTestNamePath = Path(
            currentDir,
            "${jpaCodeHelperSetting.fileRootPath}/test/kotlin",
            basepackagePath,
            "/service/impl/${serviceImplTestName}.kt"
        ).toFile().absolutePath


//    ---------------file package
        entityNamePackage = "${packageName}.entity"
        repositoryNamePackage = "${packageName}.repository"
        serviceNamePackage = "${packageName}.service"
        serviceImplNamePackage = "${packageName}.service.impl"
        entityVoNamePackage = "${packageName}.entityvo"
        entityExcelNamePackage = "${packageName}.entityexcel"
        serviceImplTestNamePackage = "${packageName}.service.impl"
//        -------------columns

        columns = table.columns.map { DBColumn(it) }.toMutableList()
        for (column in columns) {
            if (column.column.isAutoIncrement) {
                firstAutoIncrementPrpertyName = column.propertyName
                firstAutoIncrementPrpertyType = column.javaTypeName
            }
            if (String::class.simpleName == column.javaTypeName) {
                str_columns.add(column)
            }
            if (column.column.isPk) {
                pk_columns.add(column)
            }
        }
        //    --other
        today = DateUtil.today()
        if (pk_columns.isNullOrEmpty()) {
            pkJavaType = "Long"
        } else if (pk_columns.size == 1) {
            pkJavaType = pk_columns.first().javaTypeName
        } else {
            pkJavaType = "${entityName}PK"
        }
    }
}

class DBColumn(val column: Column) {
    private val logger = LoggerFactory.getLogger(DBColumn::class.java)
    var isJsonB = false
    var isJson = false
    var isUUID = false

    /** 对应的java类型*/
    var javaTypeName = ""
    var propertyName = ""

    init {
        propertyName = column.name.toCamelCase()
        if (column.typeEnum == JdbcType.OTHER && "jsonb" == column.typeName) {
            isJsonB = true
        }
        if (column.typeEnum == JdbcType.OTHER && "json" == column.typeName) {
            isJson = true
        }
        if (column.typeEnum == JdbcType.OTHER && "uuid" == column.typeName) {
            isUUID = true
        }
        configJavaTypeName()
    }

    private fun configJavaTypeName() {

        val avaTypeName1 = when (column.typeEnum) {
            ARRAY -> {
                JSONArray::class.simpleName
            }
            BIT -> {
                if ("bool" in column.typeName) Boolean::class.simpleName
                else Boolean::class.simpleName
            }
            TINYINT -> {
                Int::class.simpleName
            }
            SMALLINT -> {
                Int::class.simpleName
            }
            INTEGER -> {
                Int::class.simpleName
            }
            BIGINT -> {
                Long::class.simpleName
            }
            FLOAT -> {
                BigDecimal::class.simpleName
            }
            REAL -> {
                BigDecimal::class.simpleName
            }
            DOUBLE -> {
                BigDecimal::class.simpleName
            }
            NUMERIC -> {
                BigDecimal::class.simpleName
            }
            DECIMAL -> {
                BigDecimal::class.simpleName
            }
            CHAR -> {
                String::class.simpleName
            }
            VARCHAR -> {
                if (column.typeName.lowercase().contains("uuid")) {
                    UUID::class.qualifiedName
                } else String::class.simpleName
            }
            LONGVARCHAR -> {
                String::class.simpleName
            }
            DATE -> {
                LocalDate::class.qualifiedName
            }
            TIME -> {
                LocalTime::class.simpleName
            }
            TIMESTAMP -> {
                java.util.Date::class.simpleName
            }
            BINARY -> {
                if (column.typeName.lowercase().contains("uuid")) {
                    UUID::class.qualifiedName
                } else {
                    ByteArray::class.simpleName
                }
            }
            VARBINARY -> {
                ByteArray::class.simpleName
            }
            LONGVARBINARY -> {
                ByteArray::class.simpleName
            }
            NULL -> {
                Any::class.simpleName
            }
            OTHER -> {
                configJavaTypeNameOther()
            }
            BLOB -> {
                ByteArray::class.simpleName
            }
            CLOB -> {
                String::class.simpleName
            }
            BOOLEAN -> {
                Boolean::class.simpleName
            }
            CURSOR -> {
                Any::class.simpleName
            } // Oracle
            UNDEFINED -> {
                Any::class.simpleName
            } //
            NVARCHAR -> {
                String::class.simpleName
            }// JDK6
            NCHAR -> {
                String::class.simpleName
            }//JDK6
            NCLOB -> {
                String::class.simpleName
            }//JDK6
            STRUCT -> {
                Any::class.simpleName
            }
            JAVA_OBJECT -> {
                Any::class.simpleName
            }
            DISTINCT -> {
                Any::class.simpleName
            }
            REF -> {
                Any::class.simpleName
            }
            DATALINK -> {
                Any::class.simpleName
            }
            ROWID -> {
                Long::class.simpleName
            }// JDK6
            LONGNVARCHAR -> {
                String::class.simpleName
            }// JDK6
            SQLXML -> {
                String::class.simpleName
            }// JDK6
            DATETIMEOFFSET -> {
                OffsetDateTime::class.qualifiedName
            } // SQL Server 2008
            TIME_WITH_TIMEZONE -> {
                OffsetTime::class.simpleName
            } // JDBC 4.2 JDK8
            TIMESTAMP_WITH_TIMEZONE -> {
                ZonedDateTime::class.simpleName
            } // JDBC 4.2 JDK8
            else -> Any::class.simpleName
        }
        this.javaTypeName = avaTypeName1 ?: (String::class.simpleName.toString())
        logger.info("configJavaTypeName: ${column.tableName}:${column.name} typeName:${column.typeName} typeEnum:${column.typeEnum.name} javaTypeName:${this.javaTypeName}")
    }

    private fun configJavaTypeNameOther(): String? {
        return when (column.typeName.lowercase()) {
            "json" -> JSONObject::class.simpleName
            "jsonb" -> JSONObject::class.simpleName
            "uuid" -> UUID::class.simpleName
            else -> "Any"
        }
    }
}

fun main() {
    JpaCodeHelper.createCode(
        DbUtil.getDs(),
        JpaCodeHelperSetting.default(),
        "com.example.lean_spring_framework.autojpa",
        "test_test"
    )
//JpaCodeHelper.createCode(DbUtil.getDs(), JpaCodeHelperSetting.default(),"com.example.lean_spring_framework.autojpa")
}
