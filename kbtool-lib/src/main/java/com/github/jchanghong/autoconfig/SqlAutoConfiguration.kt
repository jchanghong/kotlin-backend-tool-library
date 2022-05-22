package com.github.jchanghong.autoconfig

import cn.hutool.core.io.resource.ResourceUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.db.DbUtil
import com.baomidou.mybatisplus.annotation.DbType
import com.github.jchanghong.autoconfig.db.mybatis.JBeanNameGenerator
import com.github.jchanghong.autoconfig.db.mybatis.JchMyBatisPlugin
import com.github.jchanghong.db.DBHelper
import com.github.jchanghong.log.kError
import com.github.jchanghong.log.kInfo
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.cache.interceptor.CacheAspectSupport
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.lang.reflect.Method
import java.util.*
import java.util.function.Supplier
import javax.annotation.PostConstruct
import javax.sql.DataSource

/** 库全局配置*/
object JchAutoConfig {
    var logSql = false
    var lockDB = false
    var swagger2BasePackage: String? = null
    val mybatisPlusConfigList = arrayListOf<MybatisPlusConfig>()
}

/** spring boot 自动配置mybatis plus。多数据库，在spring 容器启动前加入配置！！！*/
data class MybatisPlusConfig @JvmOverloads constructor(
    val beanName: String, val dataSource: DataSource,
    val mapperInterfacePackage: String,
    val mapperXMLLocations: String? = null,
    /** 初始化sql,class path 路径文件*/
    val initSql: String? = null,
    val dbType: DbType
)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(JchMyBatisPlugin::class, SqlAutoConfiguration::class)
annotation class EnableJCHAutoConfiguration

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(JchSwaggerConfig::class)
annotation class EnableJchSwaggerConfig

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(JchRedisCacheConfig::class)
annotation class EnableJchRedisCacheConfig


@Configuration
open class SqlAutoConfiguration : BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        kInfo("setApplicationContext${applicationContext.beanDefinitionCount}")
    }

    //    步骤3
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        if (beanFactory is DefaultListableBeanFactory) {
            kInfo("postProcessBeanFactory${beanFactory.beanDefinitionCount}")
            if (JchAutoConfig.mybatisPlusConfigList.size > 0) {
                for (mybatisPlusConfig in JchAutoConfig.mybatisPlusConfigList) {
                    if (!mybatisPlusConfig.initSql.isNullOrBlank()) {
                        try {
                            kInfo("执行init sql文件 ${mybatisPlusConfig.initSql}")
                            val sqlList = StrUtil.splitTrim(ResourceUtil.readUtf8Str(mybatisPlusConfig.initSql), ";")
                            for (sql in sqlList) {
                                kotlin.runCatching {
                                    kInfo(sql + "\n")
                                    DbUtil.use(mybatisPlusConfig.dataSource).execute(sql)
                                }
                            }
//                            val sqlSessionFactory =
//                                    beanFactory.getBean(mybatisPlusConfig.beanName+"_sqlSessionFactory", SqlSessionFactory::class.java)
//                            sqlSessionFactory.openSession().use {
//                                val scriptRunner = ScriptRunner(it.connection)
//                                scriptRunner.setStopOnError(true)
//                                scriptRunner.runScript(ResourceUtil.getUtf8Reader(mybatisPlusConfig.initSql))
//                            }
                        } catch (e: Exception) {
                            kError(e.localizedMessage, e)
                        }
                    }
                }
            }
        }
        setCache(beanFactory)
    }

    private fun setCache(beanFactory: ConfigurableListableBeanFactory) {
        kotlin.runCatching {
            val map: Map<String, CacheAspectSupport> = beanFactory.getBeansOfType(CacheAspectSupport::class.java)
            for ((k, v) in map) {
                v.setKeyGenerator { target: Any, method: Method, params: Array<Any?> ->
                    val s = if (params.isNullOrEmpty()) "" else params.joinToString(separator = "") {
                        it?.javaClass?.name + Objects.hashCode(it)
                    }
                    "${target::class.qualifiedName}${method.name}${s}"
                }
                kInfo("设置cache ${k} setKeyGenerator")
            }
        }
    }

    // 步骤2
//    @OptIn(ExperimentalStdlibApi::class)
    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        kInfo("postProcessBeanDefinitionRegistry${registry.beanDefinitionCount}")
        if (JchAutoConfig.mybatisPlusConfigList.size > 0) {
            kInfo("配置 AutoConfig.mybatisPlusConfigList ${JchAutoConfig.mybatisPlusConfigList.size}")
            for ((index, mybatisPlusConfig) in JchAutoConfig.mybatisPlusConfigList.withIndex()) {
                val beanName = mybatisPlusConfig.beanName
                registry.registerBeanDefinition(beanName + "_sqlSessionFactory", GenericBeanDefinition().apply {
                    instanceSupplier = Supplier {
                        DBHelper.getMybatisSqlSessionFactory(
                            mybatisPlusConfig.dbType,
                            mybatisPlusConfig.dataSource,
                            mybatisPlusConfig.mapperInterfacePackage, mybatisPlusConfig.mapperXMLLocations
                        )
                    }
                    this.isPrimary = index == 0
                })
                registry.registerBeanDefinition(beanName + "_transactionManager",
                    GenericBeanDefinition().apply {
                        instanceSupplier = Supplier {
                            DataSourceTransactionManager(mybatisPlusConfig.dataSource)
                        }
                        this.isPrimary = index == 0
                    })
                DBHelper.newMapperScannerConfigurer(
                    mybatisPlusConfig.mapperInterfacePackage,
                    beanName + "_sqlSessionFactory"
                ).apply {
                    nameGenerator = JBeanNameGenerator()
                    postProcessBeanDefinitionRegistry(registry)
                }
            }
        }
        kInfo("postProcessBeanDefinitionRegistry${registry.beanDefinitionCount}")
    }


    @PostConstruct
    fun inited() {
        kInfo("jchanghong自动配置完成==============================")
    }
}

@Configuration
@EnableSwagger2
class JchSwaggerConfig {
    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .select() // 自行修改为自己的包路径
            .apis(RequestHandlerSelectors.basePackage(JchAutoConfig.swagger2BasePackage.toString()))
            .paths(PathSelectors.any())
            .build()
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
            .title("接口文档")
            .description("接口文档") //服务条款网址
            //.termsOfServiceUrl("http://blog.csdn.net/forezp")
            .version("1.0") //.contact(new Contact("岳阳", "url", "email"))
            .build()
    }
}
