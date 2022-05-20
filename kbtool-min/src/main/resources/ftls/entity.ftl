package ${entityNamePackage}

import cn.hutool.core.util.RandomUtil
import cn.hutool.core.util.ReflectUtil
import cn.hutool.json.JSONObject
import com.github.jchanghong.kotlin.beanFillNullValues
import com.github.liaochong.myexcel.core.annotation.ExcelModel
import com.vladmihalcea.hibernate.type.json.JsonType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import org.hibernate.type.PostgresUUIDType
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaType
import java.io.Serializable

/**
* @description jpa层代码自动生成
* @author jiangchanghong
* @date ${today}
*/
@Entity
<#if pk_columns?size gt 1 >@IdClass(${entityName}PK::class)</#if>
@Table(name = "${table.tableName}"<#if table.schema?length!=0>, schema = "${table.schema}"</#if><#if table.catalog?length!=0>, catalog = "${table.catalog}"</#if>)
@TypeDefs(
value = [
TypeDef(name = "uuid", typeClass = PostgresUUIDType::class),
TypeDef(name = "json", typeClass = JsonType::class),
TypeDef(name = "jsonb", typeClass = JsonType::class)
]
)
@Component
@ExcelModel(includeAllField = true, useFieldNameAsTitle = true, dateFormat = "yyyy-MM-dd HH:mm:ss")
class ${entityName} :Serializable {
<#list columns as column>
    <#if column.column.isPk==true>
		@field:Id
    </#if>
    <#if column.column.autoIncrement==true>
		@field:GeneratedValue(strategy = GenerationType.IDENTITY)
    </#if>
    <#if column.isUUID==true>
		@field:Type(type = "uuid")
    </#if>
    <#if column.isJsonB==true>
		@field:Type(type = "jsonb")
    </#if>
    <#if column.isJson==true>
		@field:Type(type = "json")
    </#if>
	@field:Column(name = "${column.column.name}", nullable = ${column.column.isNullable?string},columnDefinition = "${column.column.typeName}")
	var ${column.propertyName}: ${column.javaTypeName}? = null

</#list>
<#if table.pkNames?size==0>
	@get:Id
	@get:Column(name = "auto_id", nullable = false,columnDefinition = "bigserial  primary key")
	@get:GeneratedValue(strategy=GenerationType.AUTO)
	var autoId: Long? = null
</#if>

override fun toString(): String =
"Entity of type: ${r"$"}{javaClass.name} ( " +
<#list columns as column>
	"${column.propertyName} = ${r"$"}${column.propertyName} " +
</#list>
")"

// constant value returned to avoid entity inequality to itself before and after it's update/merge
override fun hashCode(): Int = 42

override fun equals(other: Any?): Boolean {
if (this === other) return true
if (javaClass != other?.javaClass) return false
other as ${entityName}

<#list columns as column>
	if (${column.propertyName} != other.${column.propertyName}) return false
</#list>

return true
}

companion object {
fun newTestObject(): ${entityName} {
val entity = ${entityName}().apply { this.beanFillNullValues() }
for (kProperty1 in ${entityName}::class.declaredMemberProperties.mapNotNull { it as? KMutableProperty<*>? }) {
if (kProperty1.returnType == String::class.createType(nullable = true)) {
kProperty1.setter.call(entity, RandomUtil.randomString(8))
}
if (kProperty1.returnType == Date::class.createType(nullable = true)) {
kProperty1.setter.call(entity, Date())
}
if (kProperty1.returnType == LocalDateTime::class.createType(nullable = true)) {
kProperty1.setter.call(entity, LocalDateTime.now())
}
if (kProperty1.returnType == JSONObject::class.createType(nullable = true)) {
kProperty1.setter.call(entity, JSONObject())
}
if (kProperty1.returnType == java.sql.Date::class.createType(nullable = true)) {
kProperty1.setter.call(entity, java.sql.Date(Date().time))
}
if (kProperty1.returnType == BigDecimal::class.createType(nullable = true)) {
kProperty1.setter.call(entity, BigDecimal("0.0"))
}
if (kProperty1.returnType == Boolean::class.createType(nullable = true)) {
kProperty1.setter.call(entity, false)
}
if (kProperty1.returnType == ByteArray::class.createType(nullable = true)) {
kProperty1.setter.call(entity, ByteArray(1))
}
if (kProperty1.returnType == UUID::class.createType(nullable = true)) {
kProperty1.setter.call(entity, UUID.randomUUID())
}

}
return entity
}
}
}

<#if pk_columns?size gt 1>

	/**
	* @description jpa层代码自动生成
	* @author jiangchanghong
	* @date ${today}
	*/
	@Component
	class ${entityName}PK :Serializable {
    <#list pk_columns as column>
		@get:Id
		@field:Column(name = "${column.column.name}", nullable = ${column.column.isNullable?string},columnDefinition = "${column.column.typeName}")
		var ${column.propertyName}: ${column.javaTypeName}? = null
    </#list>

	override fun toString(): String =
	"Entity of type: ${r"$"}{javaClass.name} ( " +
    <#list pk_columns as column>
		"${column.propertyName} = ${r"$"}${column.propertyName} " +
    </#list>
	")"

	// constant value returned to avoid entity inequality to itself before and after it's update/merge
	override fun hashCode(): Int = 42

	override fun equals(other: Any?): Boolean {
	if (this === other) return true
	if (javaClass != other?.javaClass) return false
	other as ${entityName}PK
    <#list pk_columns as column>
		if (${column.propertyName} != other.${column.propertyName}) return false
    </#list>
	return true
	}
	}

</#if>

