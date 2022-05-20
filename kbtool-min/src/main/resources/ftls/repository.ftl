package ${repositoryNamePackage}

import java.util.*
import cn.hutool.json.JSONObject
import java.math.BigDecimal
import java.time.LocalDateTime
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.QueryByExampleExecutor
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Stream
import ${entityNamePackage}.*

/**
* @description jpa层代码自动生成
* @author jiangchanghong
* @date ${today}
*/
@Repository
interface ${repositoryName} : JpaRepository<${entityName}, ${pkJavaType}>,
JpaSpecificationExecutor<${entityName}>,
QueryByExampleExecutor<${entityName}> {
<#list columns as column>
	fun findAllBy${column.propertyName?cap_first}(${column.propertyName}: ${column.javaTypeName},pageable: Pageable):List<${entityName}>
</#list>

<#list str_columns as column>
	fun findAllBy${column.propertyName?cap_first}Like(${column.propertyName}: String,pageable: Pageable):List<${entityName}>
</#list>

@Query("select t from ${entityName} t ")
fun findAllStream():Stream<${entityName}>
// @Query("select t from ${entityName} as t where t.AText=?1 and t.AText like %?2%")
// fun testQuery(text: String, like: String, pageable: Pageable): List<${entityName}>

// @Query(nativeQuery = true, value = "select * from pg_all_type where a_text = ?1 and a_text like %?2%")
// fun testNativeQuery(text: String, like: String, pageable: Pageable): List<${entityName}>

// @Modifying
//  @Transactional
//  @Query("update ${entityName} t set t.AText=?1 where t.AText=?2")
// fun testUpdate(text: String, text2: String): Int
}
