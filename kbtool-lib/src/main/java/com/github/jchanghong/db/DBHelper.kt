package com.github.jchanghong.db

import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.io.IoUtil
import cn.hutool.core.text.csv.CsvReadConfig
import cn.hutool.core.text.csv.CsvRow
import cn.hutool.core.text.csv.CsvUtil
import cn.hutool.core.text.csv.CsvWriteConfig
import cn.hutool.core.util.ClassUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.db.DbUtil
import cn.hutool.db.ds.DataSourceWrapper
import cn.hutool.db.meta.MetaUtil
import com.baomidou.mybatisplus.annotation.DbType
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS
import com.baomidou.mybatisplus.core.MybatisConfiguration
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean
import com.github.jchanghong.autoconfig.db.mybatis.MybatisPlusConfig
import com.github.jchanghong.io.IOHelper
import com.github.jchanghong.json.toJsonToMap
import com.github.jchanghong.log.kInfo
import com.zaxxer.hikari.pool.HikariProxyConnection
import org.apache.ibatis.plugin.Interceptor
import org.apache.ibatis.session.ExecutorType
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.SqlSessionTemplate
import org.mybatis.spring.mapper.MapperScannerConfigurer
import org.postgresql.copy.CopyManager
import org.postgresql.core.BaseConnection
import org.postgresql.jdbc.PgConnection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.sql.Connection
import javax.sql.DataSource
import kotlin.math.max
import kotlin.streams.toList

fun main() {
    val array = ClassUtil.scanPackageBySuper("jchanghong.autoconfig.db.mybatis", Interceptor::class.java)
        .map { it.getDeclaredConstructor().newInstance() as? Interceptor }.toTypedArray()
    val db = DbUtil.use()
    val sqlSessionTemplate: SqlSessionTemplate? = null;
    val list = sqlSessionTemplate!!.selectList<String>("")
//    sqlSessionTemplate.
}

fun DataSource?.pgConnection(): PgConnection? {
    var dataSource = this
    if (dataSource is DataSourceWrapper) {
        dataSource = dataSource.raw
    }
    var connection = dataSource?.connection
    if (connection is HikariProxyConnection) {
        connection = BeanUtil.getFieldValue(connection, "delegate") as Connection?
    }
    return connection as? PgConnection
}

object DBHelper {

    /** 重置序列*/
    fun pgAlterSequence(ds: DataSource): Unit {
        val tables = MetaUtil.getTables(ds)
        for (table in tables) {
            val tb = MetaUtil.getTableMeta(ds, table)
            for (column in tb.columns) {
                if (column.isAutoIncrement) {
                    val queryNumber = DbUtil.use(ds)
                        .queryNumber(
                            """
                       select max(${column.name}) from ${tb.tableName}
                    """.trimIndent()
                        )
                    val queryNumber2 = DbUtil.use(ds)
                        .queryNumber(
                            """
                       select  nextval('${tb.tableName}_${column.name}_seq');
                    """.trimIndent()
                        )
                    if (queryNumber != null || queryNumber2 != null) {
                        val max = max(queryNumber?.toLong() ?: 0L, queryNumber2?.toLong() ?: 0L)
                        println("alter sequence if exists ${tb.tableName}_${column.name}_seq")
                        DbUtil.use(ds)
                            .execute(
                                """
                           alter sequence if exists ${tb.tableName}_${column.name}_seq
                            increment by 1
                             restart with ${max + 1L}
                        """.trimIndent()
                            )
                    }
                }
            }
        }
    }

    /** 返回数据源的key，一般是url*/
    fun dsKey(ds: DataSource): String {
        val firstOrNull =
            BeanUtil.getPropertyDescriptorMap(ds::class.java, true).entries?.firstOrNull {
                it.key.contains(
                    "url",
                    true
                )
            }
        val invoke = firstOrNull?.value?.readMethod?.invoke(ds).toString()
        kInfo(invoke)
        return ds::class.qualifiedName.toString() + ds.toString() + invoke
    }

    @JvmOverloads
            /** pg数据库 COPY $tableName to stdout with csv header*/
    fun pgCopyToFile(ds: DataSource, tableName: String, filepathname: String): Long {
        val copyManager = CopyManager(ds.pgConnection() as BaseConnection)
        val byteArrayOutputStream = File(filepathname).outputStream().buffered()
        val copyOut = copyManager.copyOut("COPY $tableName to stdout with csv header", byteArrayOutputStream)
        byteArrayOutputStream.close()
        return copyOut
    }

    @JvmOverloads
            /** pg数据库 COPY $tableName from stdin with csv header*/
    fun pgCopyFromFile(ds: DataSource, tableName: String, filepathname: String): Long {
        val copyManager = CopyManager(ds.pgConnection() as BaseConnection)
        val bufferedReader = File(filepathname).bufferedReader()
        val copyIn = copyManager.copyIn("COPY $tableName from stdin with csv header", bufferedReader)
        return copyIn
    }

    private val interceptPackages = "jchanghong.autoconfig.db.mybatis"
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    //    datasource to mapper 集合
    private val mybatisTemplateMap = HashMap<String, SqlSessionTemplate>()
    private val mybatisFatoryMap = HashMap<String, SqlSessionFactory>()
    const val DRIVER_CLASS_ORACLE = "oracle.jdbc.driver.OracleDriver"
    const val DRIVER_CLASS_POSTGRES = "org.postgresql.Driver"

    /** 包名可以为null scan mapper*/
    @JvmOverloads
    @JvmStatic
    fun getMybatisSqlSessionTemplate(
        dbType: DbType,
        dataSource: DataSource, mapperInterfacePackage: String? = null,
        mapperXMLLocations: String? = null, executorType: ExecutorType = ExecutorType.SIMPLE
    ): SqlSessionTemplate {
        val key = dsKey(dataSource)
        val sqlSessionTemplate = mybatisTemplateMap[key]
        if (sqlSessionTemplate != null) return sqlSessionTemplate
        val template = SqlSessionTemplate(
            getMybatisSqlSessionFactory(dbType, dataSource, mapperInterfacePackage, mapperXMLLocations),
            executorType
        )
        mybatisTemplateMap[key] = template
        return template
    }

    /** 包名可以为null scan mapper*/
    @JvmOverloads
    @JvmStatic
    fun getMybatisSqlSessionFactory(
        dbType: DbType,
        dataSource: DataSource, mapperInterfacePackage: String? = null,
        mapperXMLLocations: String? = null
    ): SqlSessionFactory {
        val key = dsKey(dataSource)
        val sessionFactory = mybatisFatoryMap[key]
        if (sessionFactory != null) return sessionFactory
        logger.info("开始建立mybatisSqlSessionFactory")
        // TODO 使用 MybatisSqlSessionFactoryBean 而不是 SqlSessionFactoryBean
        val factory = MybatisSqlSessionFactoryBean()
        val configuration = MybatisConfiguration()
        factory.configuration = configuration
        factory.setDataSource(dataSource)
        factory.vfs = SpringBootVFS::class.java
        val list = ClassUtil.scanPackageBySuper(interceptPackages, Interceptor::class.java)
            .mapNotNull { it.getDeclaredConstructor().newInstance() as? Interceptor }
        factory.setPlugins(
            *list.toTypedArray(), MybatisPlusConfig.paginationInterceptor(dbType)
        )
//        if (StringUtils.hasLength(typeAliasesPackage)) {
//            factory.setTypeAliasesPackage(typeAliasesPackage)
//        }
        factory.setTypeHandlersPackage(interceptPackages)

        if (!mapperXMLLocations.isNullOrBlank()) {
            IOHelper.resolveMapperLocations(mapperXMLLocations)?.let {
                factory.setMapperLocations(*it)
            }
        }
        // TODO 此处必为非 NULL
        val globalConfig = GlobalConfigUtils.defaults()
        // TODO 注入填充器
//        globalConfig.metaObjectHandler = null

        // TODO 注入主键生成器
//        globalConfig.dbConfig.keyGenerator = null

        // TODO 注入sql注入器
//        globalConfig.sqlInjector = null

        // TODO 注入ID生成器
//        globalConfig.identifierGenerator = null
        // TODO 设置 GlobalConfig 到 MybatisSqlSessionFactoryBean
        factory.setGlobalConfig(globalConfig)
        if (!mapperInterfacePackage.isNullOrBlank()) {
            configuration.mapperRegistry.addMappers(mapperInterfacePackage)
        }
        val sqlSessionFactory = factory.getObject()
        mybatisFatoryMap[key] = sqlSessionFactory!!
        logger.info("建立 mybatisSqlSessionFactory完成 $dataSource")
        return sqlSessionFactory
    }

    @JvmOverloads
    fun <T> getMybatisMapper(
        dbType: DbType,
        type: Class<T>,
        dataSource: DataSource,
        executorType: ExecutorType = ExecutorType.SIMPLE
    ): T {
        val sessionTemplate = getMybatisSqlSessionTemplate(dbType, dataSource, executorType = executorType)
        sessionTemplate.configuration.mapperRegistry
            .addMapper(type)
        return sessionTemplate.getMapper(type)
    }

    @JvmStatic
    fun newMapperScannerConfigurer(basePackage: String, SqlSessionFactoryBeanName: String): MapperScannerConfigurer {
        val configurer = MapperScannerConfigurer()
        configurer.setBasePackage(basePackage)
        configurer.setSqlSessionFactoryBeanName(SqlSessionFactoryBeanName)
        return configurer
    }

    fun <T : Any> csvToBeanList(csv: String, clazz: Class<T>): List<T> {
        val list = mutableListOf<CsvRow>()
        CsvUtil.getReader(CsvReadConfig.defaultConfig().apply {
            this.setContainsHeader(true)
        })
            .read(csv.reader()) { row ->
                list.add(row)
            }
        return list.parallelStream().map {
            val mapToBean = BeanUtil.toBeanIgnoreError(it.fieldMap, clazz)
            mapToBean
        }.toList()
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun <T : Any> beanListToCsv(list: List<T>): String {
        val one = list.firstOrNull() ?: return ""
        val map = BeanUtil.beanToMap(one, true, false)
        val byteArrayOutputStream = ByteArrayOutputStream(1000000)
        val readerUTF8 = byteArrayOutputStream.writer(Charsets.UTF_8)
        val csvWriter = CsvUtil.getWriter(readerUTF8, CsvWriteConfig.defaultConfig().apply {})
        csvWriter.write(map.keys.toTypedArray())
        for (t in list) {
            val beanMap: MutableMap<String, Any?> = BeanUtil.beanToMap(t, true, false)
            val typedArray = beanMap.values.map { it?.toString() }.toList().toTypedArray()
            csvWriter.write(typedArray)
        }
        return byteArrayOutputStream.toByteArray().decodeToString()
    }

    fun <T : Any> csvFileToBeanList(csvFilePath: String, clazz: Class<T>): List<T> {
        val bufferedReader = File(csvFilePath).bufferedReader()
        val list = mutableListOf<CsvRow>()
        CsvUtil.getReader(CsvReadConfig.defaultConfig().apply {
            this.setContainsHeader(true)
        })
            .read(bufferedReader) { row ->
                list.add(row)
            }
        val toList = list.parallelStream().map {
            val mapToBean = BeanUtil.toBeanIgnoreError(it.fieldMap, clazz)
            mapToBean
        }.toList()
        IoUtil.close(bufferedReader)
        return toList
    }

    fun <T : Any> beanListToCsvFile(list: List<T>, csvFilePath: String) {
        val bufferedWriter = File(csvFilePath).bufferedWriter()
        val one = list.firstOrNull() ?: return
        val map = BeanUtil.beanToMap(one, true, false)
        val csvWriter = CsvUtil.getWriter(bufferedWriter, CsvWriteConfig.defaultConfig().apply {})
        val keylist = map.keys.toTypedArray()
        csvWriter.write(keylist)
        val toList = list.parallelStream().map {
            val beanMap = it.toJsonToMap()
            keylist.map { key -> beanMap!!.getStr(StrUtil.toCamelCase(key)) }
        }.toList()
        csvWriter.write(toList)
        IoUtil.close(bufferedWriter)
    }
}
