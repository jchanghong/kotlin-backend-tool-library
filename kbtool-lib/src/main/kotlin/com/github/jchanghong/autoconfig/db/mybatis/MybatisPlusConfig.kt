package com.github.jchanghong.autoconfig.db.mybatis

import com.baomidou.mybatisplus.annotation.DbType
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor


/**
 *
 * @author : jiangchanghong
 *
 * @version : 2020-03-02 15:07
 **/
object MybatisPlusConfig {
    // 最新版
//    @Bean
    fun paginationInterceptor(dbType: DbType): MybatisPlusInterceptor {
        val interceptor = MybatisPlusInterceptor()
        interceptor.addInnerInterceptor(PaginationInnerInterceptor(dbType))
//        interceptor.addInnerInterceptor(PaginationInnerInterceptor(DbType.ORACLE))
//        interceptor.addInnerInterceptor(PaginationInnerInterceptor(DbType.ORACLE_12C))
        return interceptor
    }
//    fun paginationInterceptor(): PaginationInnerInterceptor {
//        val paginationInterceptor = PaginationInnerInterceptor()
//        // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
//        // paginationInterceptor.setOverflow(false);
//        // 设置最大单页限制数量，默认 500 条，-1 不受限制
////        paginationInterceptor.setLimit(-1);
//        // 开启 count 的 join 优化,只针对部分 left join
////        paginationInterceptor.setCountSqlParser(JsqlParserCountOptimize(true))
//        return paginationInterceptor
//    }
}
