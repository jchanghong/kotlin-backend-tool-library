package com.github.jchanghong.autoconfig.db.mybatis;

/**
\* Created with IntelliJ IDEA.
\* User: jiang
\* Date: 2020/1/5
\* Time: 14:59
\*/
//@Intercepts(
//    value = [Signature(type = ResultSetHandler::class, method = "handleResultSets", args = [Statement::class]),
//        Signature(type = ResultSetHandler::class, method = "handleOutputParameters", args = [CallableStatement::class])
//    ]
//)
//class ResultSetHandlerPlugin : Interceptor {
//    override fun intercept(invocation: Invocation): Any? {
//        var proceed = invocation.proceed()
//        if (invocation.method.name == "handleResultSets") {
//            handleResultSets(invocation.args[0] as Statement?, proceed)
//        }
//        return proceed
//    }
//
//    fun handleResultSets(stmt: Statement?, result: Any?) {
//        if (stmt == null || result == null) {
//            return
//        }
//    }
//}

fun main() {

}
// <E> List<E> handleResultSets(Statement stmt) throws SQLException;
//
//  <E> Cursor<E> handleCursorResultSets(Statement stmt) throws SQLException;
//
//  void handleOutputParameters(CallableStatement cs) throws SQLException;
