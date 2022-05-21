package org.apache.myfaces.testcode

import cn.hutool.db.Entity
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.getForObject

fun main() {
    val restTemplate = RestTemplateBuilder().build()
    val entity = restTemplate.getForObject<Entity>(
        "http://55555.1.43.83:80/check/imap/deviceAllInfo?indexCode=50000700291329990001&resourceType=CAMERA"
    )
    println(entity)
}
