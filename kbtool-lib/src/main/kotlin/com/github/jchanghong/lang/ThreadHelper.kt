package com.github.jchanghong.lang

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
create at 2022-02-2022/2/10-11:46
@author jiangchanghong
 */
object ThreadHelper {
    @JvmStatic
    val newScheduledThreadPool2 by lazy { Executors.newScheduledThreadPool(2) }

    @JvmStatic
    val newFixedThreadPool4 by lazy { Executors.newFixedThreadPool(4) }

    @JvmStatic
    val newCachedThreadPool by lazy { Executors.newCachedThreadPool() }
}

fun main() {
    ThreadHelper.newScheduledThreadPool2.scheduleWithFixedDelay(
        Runnable {
            runCatching {
                println("hello")
            }
        },
        1000, 1000, TimeUnit.MILLISECONDS
    )
    ThreadHelper.newCachedThreadPool.submit {
        println("==================")
    }

}
