package com.github.jchanghong.coroutinne

import cn.hutool.core.thread.ThreadUtil
import cn.hutool.http.HttpUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

fun main() {
    val value = CoroutineScope(Dispatchers.IO)

    for (i in (1..10)) {
        value.async {
            println(Thread.currentThread().name)
            gethttp()
        }
            .invokeOnCompletion { println(i) }
    }
    ThreadUtil.sleep(20000)
//    println(measureTimeMillis {
//       runBlocking {
//           (1..1000).map {  value.async {
//               gethttp()
//           }
//           }.awaitAll()
//       }
//    })
}

private suspend fun sus1(): Long {
    delay(1000)
    println("Hello" + Thread.currentThread().name)
    return 1
}

fun gethttp(): String {
    println(Thread.currentThread().name)
    ThreadUtil.sleep(1000)
    return HttpUtil.get("https://www.baidu.com/?tn=44048691_1_oem_dg")
}
