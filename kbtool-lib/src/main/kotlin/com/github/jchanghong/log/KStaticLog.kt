package com.github.jchanghong.log

import cn.hutool.core.lang.caller.CallerUtil
import cn.hutool.core.util.StrUtil
import org.slf4j.LoggerFactory

internal class TestLogger {
    fun log(): Unit {
        kInfo("sasaaaaaa")
    }
}

/**
 * 静态日志类，用于在不引入日志对象的情况下打印日志
 *
 * @author Looly
 */
fun main() {
    TestLogger().log()
    kError("sasas", NullPointerException("sasa"))
    kInfo("sasa")
    kDebug("sasasas")
}

/**
 * Debug等级日志，小于Info
 *
 * @param log 日志对象
 * @param format 格式文本，{} 代表变量
 * @param arguments 变量对应的参数
 */
fun kDebug(format: String?, vararg arguments: Any?) {
    LoggerFactory.getLogger(CallerUtil.getCallerCaller()).debug(StrUtil.format(format, arguments))
}


/**
 * Info等级日志，小于Warn
 *
 * @param log 日志对象
 * @param format 格式文本，{} 代表变量
 * @param arguments 变量对应的参数
 */
fun kInfo(format: String?, vararg arguments: Any?) {
    LoggerFactory.getLogger(CallerUtil.getCallerCaller()).info(StrUtil.format(format, arguments))
}


/**
 * Error等级日志<br></br>
 *
 * @param log 日志对象
 * @param e 需在日志中堆栈打印的异常,可以为null
 * @param format 格式文本，{} 代表变量
 * @param arguments 变量对应的参数
 */
fun kError(format: String?, e: Throwable?, vararg arguments: Any?) {
    if (e == null) {
        LoggerFactory.getLogger(CallerUtil.getCallerCaller()).error(StrUtil.format(format, arguments))
    } else {
        LoggerFactory.getLogger(CallerUtil.getCallerCaller()).error(StrUtil.format(format, arguments), e)
    }
}
