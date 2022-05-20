package com.github.jchanghong.random

import cn.hutool.core.util.RandomUtil

object RandomHelper {
    private val list = ('a'..'z').toList() + ('A'..'Z').toList()
    fun randomWord(): String {
        return (1..RandomUtil.randomInt(3, 10))
            .map { list[RandomUtil.randomInt(0, list.size)] }.joinToString(separator = "")
            .lowercase()
    }

    fun randomWordList(max: Int = 100): String {
        return (1..RandomUtil.randomInt(1, max)).map { randomWord() }
            .joinToString(separator = " ")
    }
}

fun main() {
    for (i in (1..20)) {
        println(RandomHelper.randomWordList())
    }
}
