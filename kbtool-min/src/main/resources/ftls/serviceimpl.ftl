package ${serviceImplNamePackage}

import com.github.liaochong.myexcel.core.DefaultExcelBuilder
import org.apache.poi.ss.usermodel.Workbook
import org.springframework.data.domain.*
import org.springframework.stereotype.Service
import ${entityNamePackage}.*
import ${repositoryNamePackage}.*
import ${serviceNamePackage}.*
import ${entityExcelNamePackage}.*

/**
* @description jpa层代码自动生成
* @author jiangchanghong
* @date ${today}
*/
@Service
class ${serviceImplName}(val ${repositoryName?uncap_first}: ${repositoryName}) : ${serviceName} {
override fun insertNNumberTestData(number: Int): List<${entityName}> {
check(number > 0)
val list = (1..number).map { ${entityName}.newTestObject().apply {
<#if firstAutoIncrementPrpertyName!="">
	this.${firstAutoIncrementPrpertyName} = <#if firstAutoIncrementPrpertyType=="Long">null<#else>null</#if>
</#if>
}
}
val saveAll = ${repositoryName?uncap_first}.saveAll(list)
return saveAll
}

override fun findPage(entity: ${entityName}, pageable: Pageable): Page<${entityName}> {
val exampleMatcher = ExampleMatcher.matching()
val example = Example.of(entity, exampleMatcher)
val page = ${repositoryName?uncap_first}.findAll(example, pageable)
return page
}

override fun findAll(entity: ${entityName}, sort: Sort?): List<${entityName}> {
val exampleMatcher = ExampleMatcher.matching()
val example = Example.of(entity, exampleMatcher)
val list =
if (sort == null) ${repositoryName?uncap_first}.findAll(example) else ${repositoryName?uncap_first}.findAll(example, sort)
return list
}

override fun excelAll(): Workbook? {
try {
val findAll = ${repositoryName?uncap_first}.findAll(PageRequest.of(0,1111)).content.map { ${entityExcelName}.newObjectFromDbEntity(it) }
val build = DefaultExcelBuilder.of(${entityExcelName}::class.java).build(findAll)
return build
} catch (e: Exception) {
return  null
}
}
//val list: MutableList<${entityName}> = ${repositoryName?uncap_first}.findAll { root, query, criteriaBuilder ->
//  val equal = criteriaBuilder.equal(root.get
<String>(JpaPgAllTypeEntity::aBoolean.name), true)
	// val like = criteriaBuilder.like(root.get(JpaPgAllTypeEntity::aText.name), "%s%")
	// val like2 = criteriaBuilder.like(root.get(JpaPgAllTypeEntity::aText.name), "%s%")
	// criteriaBuilder.and(equal, like,like2)
	// }
	//val factorySupport = JpaRepositoryFactory(Persistence.createEntityManagerFactory("default").createEntityManager())
	// val repository = factorySupport.getRepository(JpaPgAllTypeRepository::class.java)
	// val findAllByaTextLike = repository.findAllByaTextLike("sasa", Pageable.unpaged())
	// for (entity in findAllByaTextLike) {
	// println(entity)
	//}
	}
