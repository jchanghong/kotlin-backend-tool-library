package com.github.jchanghong.str

import cn.hutool.core.util.StrUtil

fun main() {
	println(teststr)
	println(teststr.length)
	println(StrUtil.removeAllLineBreaks(teststr).chunked(130).joinToString("\n"))
}

private val teststr = """
    sdasdasdasd
    select sasasas,
    dasdadasdasd,dasdasdasdasd,
    dasdasdasd,
    dasdadasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
    dadasdasdasd,
""".trimIndent()

fun String.toCamelCase(): String {
	return StrUtil.toCamelCase(this)
}

fun String.toUnderlineCase(): String {
	return StrUtil.toUnderlineCase(this)
}

fun String.upperFirst(): String {
	return StrUtil.upperFirst(this)
}

object StrHelper {
}
