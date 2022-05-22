package com.github.jchanghong.str

import cn.hutool.core.util.StrUtil

fun String.toCamelCase(): String {
    return StrUtil.toCamelCase(this)
}

fun String.toUnderlineCase(): String {
    return StrUtil.toUnderlineCase(this)
}

fun String.upperFirst(): String {
    return StrUtil.upperFirst(this)
}
