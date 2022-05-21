package com.github.jchanghong.lang

import cn.hutool.core.date.DateUtil
import cn.hutool.system.SystemUtil
import com.github.rholder.retry.BlockStrategies
import com.github.rholder.retry.RetryerBuilder
import com.github.rholder.retry.StopStrategies
import com.github.rholder.retry.WaitStrategies
import org.springframework.retry.support.RetryTemplateBuilder
import java.util.Date
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * guava 重试。
create at 2022-02-2022/2/10-11:46
@author jiangchanghong
 */
object RetryHelper {
    /** */
    fun <V> newRetryerBuilder(): RetryerBuilder<V> {
        return RetryerBuilder.newBuilder<V>()
            .withStopStrategy(StopStrategies.stopAfterAttempt(6))
            .withBlockStrategy(BlockStrategies.threadSleepStrategy())
            .withWaitStrategy(WaitStrategies.fibonacciWait(1000, 30, TimeUnit.MINUTES))
            .retryIfExceptionOfType(Exception::class.java)
    }
}

fun main() {
    println(SystemUtil.getRuntimeInfo().runtime.availableProcessors())
    val retryer = RetryHelper.newRetryerBuilder<Int>()
        .withStopStrategy(StopStrategies.stopAfterAttempt(3))
        .build()
    var w=0L
    val call = retryer.call {
        var l = Date().time - w
        w=Date().time
        println(l)
        1 / "0".toInt() + 3 }
    println(call)
}
