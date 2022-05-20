package com.github.jchanghong.lang

import cn.hutool.core.util.RandomUtil
import kotlin.system.measureTimeMillis

/** 执行n次的平均时间*/
fun exeTimeAvg(time: Int, action: () -> Unit): Double {
    return (1..time).map { measureTimeMillis(action) }.map { it.toInt() }.average()
}

fun main() {
    println(exeTimeAvg(10) {
        (1..10000).forEach {
            RandomUtil.randomString(1000)
        }
    })
}
