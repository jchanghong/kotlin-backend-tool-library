package com.github.jchanghong.autoconfig.db.mybatis

import cn.hutool.core.util.StrUtil
import com.github.jchanghong.autoconfig.JchAutoConfig
import org.apache.ibatis.executor.statement.StatementHandler
import org.apache.ibatis.plugin.Interceptor
import org.apache.ibatis.plugin.Intercepts
import org.apache.ibatis.plugin.Invocation
import org.apache.ibatis.plugin.Signature
import org.apache.ibatis.session.ResultHandler
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.Statement

/**
\* Created with IntelliJ IDEA.
\* User: jiang
\* Date: 2020/1/5
\* Time: 14:59
\*/
@Intercepts(
    value = [Signature(type = StatementHandler::class, method = "prepare", args = [Connection::class, Integer::class]),
        Signature(type = StatementHandler::class, method = "parameterize", args = [Statement::class]),
        Signature(type = StatementHandler::class, method = "batch", args = [Statement::class]),
        Signature(type = StatementHandler::class, method = "update", args = [Statement::class]),
        Signature(type = StatementHandler::class, method = "query", args = [Statement::class, ResultHandler::class]),
        Signature(type = StatementHandler::class, method = "prepare", args = [Connection::class, Integer::class])
    ]
)
class StatementHandlerPlugin : Interceptor {
    private val logger = LoggerFactory.getLogger(StatementHandlerPlugin::class.java)!!
    private val methodSet = hashSetOf("batch", "update", "query")
    private val regex = """(SELECT)([^&]+)(FROM)""".toRegex()
    override fun intercept(invocation: Invocation?): Any? {
        if (invocation?.method?.name in methodSet) {
            val statement = invocation?.args?.get(0) as? Statement?
            if (statement != null && JchAutoConfig.logSql) {
                var sql = ""
                if (statement.toString().contains("Hikari")) {
                    sql =
                        statement.toString().removeRange(0, "HikariProxyPreparedStatement@1965473700 wrapping".length)
                } else {
                    sql = statement.toString()
                }
                var removeAllLineBreaks = StrUtil.removeAllLineBreaks(sql)
                removeAllLineBreaks = removeAllLineBreaks
                    .replace(regex, "$1 * $3")
                val string = removeAllLineBreaks.chunked(130).joinToString("\n")
                logger.info("\n" + string)
            }
        }
        return invocation?.proceed()
    }
}
//Statement prepare(Connection connection, Integer transactionTimeout)
//      throws SQLException;
//
//  void parameterize(Statement statement)
//      throws SQLException;
//
//  void batch(Statement statement)
//      throws SQLException;
//
//  int update(Statement statement)
//      throws SQLException;
//
//  <E> List<E> query(Statement statement, ResultHandler resultHandler)
//      throws SQLException;
//
//  <E> Cursor<E> queryCursor(Statement statement)
//      throws SQLException;
//
//  BoundSql getBoundSql();
//
//  ParameterHandler getParameterHandler();
