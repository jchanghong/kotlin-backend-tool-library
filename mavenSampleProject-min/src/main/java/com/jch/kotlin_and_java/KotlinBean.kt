package com.jch.kotlin_and_java

import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class KotlinBean {
    var name: String = "kotlin name!!!"

    @PostConstruct
    fun callJava() {
        println(JavaBean().name)
        JavaBean().callKotlin()
    }
}
