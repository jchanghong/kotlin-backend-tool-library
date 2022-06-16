package com.jch

import java.util.LinkedList

/**
create at 2022-06-2022/6/7-15:32
@author jiangchanghong
 */
fun main() {
    val path = LinkedList<Int>()
    bt(listOf(1,2,3),path)
}

fun bt(listOf: List<Int>, path: java.util.LinkedList<Int>) {
    if (path.size ==listOf.size) {
        println(path.toString())
        return
    }
    for (i in (0..1)) {
        path.addLast(i)
        bt(listOf,path)
        path.removeLast()
    }
}
