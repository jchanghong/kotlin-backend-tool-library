package com.github.jchanghong.http

import cn.hutool.json.JSONUtil
import okhttp3.Call
import org.slf4j.LoggerFactory

var jch_okhttp_debug = false
private val okhttp_log = LoggerFactory.getLogger("jch_okhttp")
fun okhttpLog(log: Any?) {
	if (jch_okhttp_debug) {
		okhttp_log.info(log.toString())
	}
}

/** 可能返回空*/
fun Call.executeForUtf8(): String {
	val execute = this.execute().use {
		if (!it.isSuccessful) ""
		else it.body?.string() ?: ""
	}
	return execute
}

/** {"type":0,"code":"0", code=0*/
fun String?.pviaOkBody(): Boolean {
	if (this.isNullOrBlank()) return false
	return try {
		val code = JSONUtil.parseObj(this).getStr("code")
		"0" == code
	} catch (e: Exception) {
		false
	}
}
