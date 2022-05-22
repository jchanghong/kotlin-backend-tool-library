package com.github.jchanghong.lang

import cn.hutool.core.util.RandomUtil
import cn.hutool.system.SystemUtil
import com.github.jchanghong.log.kInfo
import com.github.rholder.retry.*
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * guava 重试。
create at 2022-02-2022/2/10-11:46
@author jiangchanghong
 */
object RetryHelper {
    private var retryListener: RetryListener? = null
    fun withRetryListener(retryListener: RetryListener) {
        this.retryListener = retryListener
    }

    private val fixedThreadPool by lazy { Executors.newFixedThreadPool(SystemUtil.getRuntimeInfo().runtime.availableProcessors()) }

    /** 重试4次 */
    private val retry4TimesRetryer by lazy {
        val builder = RetryerBuilder.newBuilder<Any?>()
            .retryIfException()
            .retryIfResult { it == null }
            .withStopStrategy(StopStrategies.stopAfterAttempt(4))
            .withBlockStrategy(BlockStrategies.threadSleepStrategy())
            .withWaitStrategy(WaitStrategies.incrementingWait(1, TimeUnit.MILLISECONDS, 1, TimeUnit.MILLISECONDS))
        if (retryListener != null) {
            builder.withRetryListener(retryListener!!)
        }
        builder.build()
    }

    //    一直重试
    private val neverStopRetryer by lazy {
        val builder = RetryerBuilder.newBuilder<Any?>()
            .retryIfException()
            .retryIfResult { it == null }
            .withStopStrategy(StopStrategies.neverStop())
            .withBlockStrategy(BlockStrategies.threadSleepStrategy())
            .withWaitStrategy(WaitStrategies.fibonacciWait(1, 60, TimeUnit.MINUTES))
        if (retryListener != null) {
            builder.withRetryListener(retryListener!!)
        }
        builder
            .build()
    }

    /** 异常或者返回null，会重试最多4次*/
    fun submitByRetry4Times(callable: Callable<Any?>): Future<Any?> {
        val future = fixedThreadPool.submit(Callable {
            retry4TimesRetryer.call(callable)
        })
        return future
    }

    /** 异常或者返回null，会重试最多N次*/
    fun submitByRetryNTimes(callable: Callable<Any?>): Future<Any?> {
        val future = fixedThreadPool.submit(Callable {
            neverStopRetryer.call(callable)
        })
        return future
    }
}

fun main() {
    var w = 0L
    RetryHelper.withRetryListener(object : RetryListener {
        override fun <V : Any?> onRetry(attempt: Attempt<V>) {
            if (attempt.hasResult() && attempt.get() != null) {
                kInfo("尝试${attempt.attemptNumber}次后成功")
            }
        }
    })
    val callable: Callable<Any?> = Callable {
        val l = Date().time - w
        w = Date().time
//        kInfo(l.toString())
        val randomInt = RandomUtil.randomInt(10)
//        println(randomInt)
        val i = if (randomInt <= 5) {
            1 / "0".toInt() + 3
        } else {
            1
        }
        i
    }
    for (future in (1..200).map { RetryHelper.submitByRetryNTimes(callable) }) {
        check(future.get().toString() == "1")
    }
    println("test end")
}
