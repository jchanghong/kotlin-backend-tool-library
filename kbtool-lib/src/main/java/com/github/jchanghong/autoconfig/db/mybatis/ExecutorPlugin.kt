package com.github.jchanghong.autoconfig.db.mybatis;


/**
\* Created with IntelliJ IDEA.
\* User: jiang
\* Date: 2020/1/5
\* Time: 14:58
\*/
//
//
//@Intercepts(
//    value = [Signature(type = Executor::class, method = "update", args = [MappedStatement::class, Any::class]),
//        Signature(
//            type = Executor::class,
//            method = "query",
//            args = [MappedStatement::class, Any::class, RowBounds::class, ResultHandler::class, CacheKey::class, BoundSql::class]
//        ),
//        Signature(
//            type = Executor::class,
//            method = "query",
//            args = [MappedStatement::class, Any::class, RowBounds::class, ResultHandler::class]
//        ),
//        Signature(type = Executor::class, method = "commit", args = [Boolean::class]),
//        Signature(type = Executor::class, method = "rollback", args = [Boolean::class]),
//        Signature(type = Executor::class, method = "close", args = [Boolean::class])
//    ]
//)
//class ExecutorPlugin : Interceptor {
//
//    override fun intercept(invocation: Invocation): Any? {
//        if (invocation.method.name == "update" && AutoConfig.lockDB) {
//            return 0;
//        }
//        var proceed = invocation.proceed()
////        log.info(invocation.target.javaClass.name+":"+invocation.method.name)
//        return proceed
//    }
//
//
//}
//  int update(MappedStatement ms, Object parameter) throws SQLException;
//
//  <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException;
//
//  <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException;
//
//  <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException;
//
//  List<BatchResult> flushStatements() throws SQLException;
//
//  void commit(boolean required) throws SQLException;
//
//  void rollback(boolean required) throws SQLException;
//
//  CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql);
//
//  boolean isCached(MappedStatement ms, CacheKey key);
//
//  void clearLocalCache();
//
//  void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType);
//
//  Transaction getTransaction();
//
//  void close(boolean forceRollback);
//
//  boolean isClosed();
//
//  void setExecutorWrapper(Executor executor)
