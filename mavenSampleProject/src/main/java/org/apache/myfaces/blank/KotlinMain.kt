package org.apache.myfaces.blank

import com.github.jchanghong.json.toJsonStr
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component

class KotlinMain

@Component
data class VO1(var id: String? = null)
class VO2 : VO1(id = "")

fun main() {
//    DbUtil.getDs()
    println(VO1("sasa").toJsonStr())
    println(VO2().toJsonStr())
    println("shasasas")
    println(HelloWorldController.fromjava())
    runApplication<KotlinMain>()
}
