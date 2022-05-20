package com.github.jchanghong.autoconfig.db.mybatis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
\* Created with IntelliJ IDEA.
\* User: jiang
\* Date: 2020/1/5
\* Time: 14:57
\*/
@Configuration
open class JchMyBatisPlugin {
    @Bean
    open fun jsonHandler(): PGJsonTypeHandler {
        return PGJsonTypeHandler()
    }
//    @Bean
//    open fun executorPlugin(): ExecutorPlugin = ExecutorPlugin()

//    @Bean
//    open fun handlerPlugin(): ParameterHandlerPlugin = ParameterHandlerPlugin()

//    @Bean
//    open fun resultSetHandlerPlugin(): ResultSetHandlerPlugin = ResultSetHandlerPlugin()

    @Bean
    open fun statementHandlerPlugin(): StatementHandlerPlugin = StatementHandlerPlugin()
}
