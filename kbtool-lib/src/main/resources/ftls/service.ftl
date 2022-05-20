package ${serviceNamePackage}

import org.apache.poi.ss.usermodel.Workbook
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import ${entityNamePackage}.*
/**
* @description jpa层代码自动生成
* @author jiangchanghong
* @date ${today}
*/
interface ${serviceName} {
fun insertNNumberTestData(number: Int): List<${entityName}>
fun findPage(entity: ${entityName}, pageable: Pageable): Page<${entityName}>
fun findAll(entity: ${entityName}, sort: Sort? = null): List<${entityName}>
fun excelAll():Workbook?
}
