package com.jch

import com.github.jchanghong.gson.toJsonStr
import com.github.jchanghong.kotlin.toDateJdk7OrNull
import com.github.jchanghong.kotlin.toLocalDateTime
import com.github.jchanghong.kotlin.toStrOrNow

fun main() {
    val date = "2022-05-05 00:00:00".toDateJdk7OrNull()
    println(date.toStrOrNow())
    println(date.toLocalDateTime().toStrOrNow())
    println(date.toJsonStr())
}
