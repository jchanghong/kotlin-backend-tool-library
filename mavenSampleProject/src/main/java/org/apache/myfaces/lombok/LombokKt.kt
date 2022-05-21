package org.apache.myfaces.lombok

object LombokKt {
    fun test() {
        val java = LombokJava().setName("sasasa")
        println(java.toString())
    }
}

fun main() {
    val java = LombokJava().setName("sasasa")
    println(java.toString())
}
