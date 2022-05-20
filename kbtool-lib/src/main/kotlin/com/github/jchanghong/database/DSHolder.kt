package com.github.jchanghong.database

import cn.hutool.db.DbUtil
import org.springframework.boot.jdbc.DataSourceBuilder
import javax.sql.DataSource

/**
 * <p>
 * DSHolder
 * </p>
 * @author : jiangchanghong
 * @version : 2019-08-26 11:18
 */
object DSHolder {
    private val map: HashMap<String, DataSource> = hashMapOf()
    fun get(url: String, user: String, pass: String): DataSource {
        map[url]?.let { return it }
        val dataSource = DataSourceBuilder.create()
            .url(url).username(user).password(pass).build()
        map[url] = dataSource
        return dataSource
    }


    val check_db by lazy { DbUtil.getDs("check_db")!! }
}
