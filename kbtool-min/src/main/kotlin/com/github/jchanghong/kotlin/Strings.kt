package com.github.jchanghong.kotlin

import cn.hutool.core.codec.Base64
import cn.hutool.core.util.StrUtil
import cn.hutool.core.util.ZipUtil
import java.nio.charset.StandardCharsets

fun main() {
	println("".isNotNUllOrBlank2())
	println("Asasa".removePreAndLowerFirst(null))
}

fun String?.isNotNUllOrBlank2() = !this.isNullOrBlank()

fun String?.toCamelCase(): String {
	this ?: return ""
	return StrUtil.toCamelCase(this)
}

fun String?.toUnderlineCase(): String {
	this ?: return ""
	return StrUtil.toUnderlineCase(this)
}

fun String?.removePreAndLowerFirst(prefix: String?): String {
	this ?: return ""
	return StrUtil.removePreAndLowerFirst(this, prefix ?: "")
}

fun String.toGzipUtf8(): String {
	return Base64.encode(ZipUtil.gzip(this, StandardCharsets.UTF_8.toString()))
}

fun String.toUnGzipUtf8(): String {
	return ZipUtil.unGzip(Base64.decode(this), StandardCharsets.UTF_8.toString())
}



