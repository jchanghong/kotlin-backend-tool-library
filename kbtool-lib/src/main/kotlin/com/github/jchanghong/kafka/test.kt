package com.github.jchanghong.kafka

import cn.hutool.http.HttpUtil

fun main() {

    println(HttpUtil.get("https://tieba.baidu.com/p/7124400044?pn=32"))
}

