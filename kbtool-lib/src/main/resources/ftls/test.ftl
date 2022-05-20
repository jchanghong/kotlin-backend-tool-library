package ${serviceImplTestNamePackage}

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.transaction.annotation.Transactional
import org.junit.jupiter.api.BeforeEach
import ${entityNamePackage}.*
import ${repositoryNamePackage}.*
import ${serviceNamePackage}.*
/**
* @description jpa层代码自动生成
* @author jiangchanghong
* @date ${today}
*/
@SpringBootTest
internal class ${serviceImplTestName} {

@Autowired
lateinit var ${serviceName?uncap_first}: ${serviceName}

internal fun each() {
println("Before each insert one ........"+${serviceName?uncap_first}.insertNNumberTestData(1).toString())
}
@Test
@Transactional
fun insertNNumberTestData() {
each()
${serviceName?uncap_first}.insertNNumberTestData(100)
}

@Test
@Transactional
fun findPage() {
each()
val page =
${serviceName?uncap_first}.findPage(${entityName}().apply {  }, PageRequest.of(0, 10))
assert(page.content.isNotEmpty())
}
@Test
@Transactional
fun excelAll(): Unit {
each()
assert(${serviceName?uncap_first}.excelAll()!=null)
}
}