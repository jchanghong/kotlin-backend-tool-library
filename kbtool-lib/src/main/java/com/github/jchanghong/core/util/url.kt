package com.github.jchanghong.core.util

import cn.hutool.core.net.URLDecoder
import cn.hutool.core.net.URLEncodeUtil

fun String?.toEncodedUrl(): String {
    this ?: return ""
    return URLEncodeUtil.encode(this)
}
fun String?.toDecodedUrl(): String {
    this ?: return ""
    return URLDecoder.decode(this, Charsets.UTF_8)
}
/**
create at 2022-05-2022/5/23-14:50
@author jiangchanghong
 */
internal fun main() {
    val url = "https://www.hutool.cn/docs/#/core/%E5%B7%A5%E5%85%B7%E7%B1%BB/URL%E5%B7%A5%E5%85%B7-URLUtil"
    println(url.toDecodedUrl())
    println(url.toDecodedUrl().toEncodedUrl())
}
