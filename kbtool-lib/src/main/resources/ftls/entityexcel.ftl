package ${entityExcelNamePackage}

import cn.hutool.core.util.RandomUtil
import cn.hutool.core.util.ReflectUtil
import cn.hutool.json.JSONObject
import com.github.jchanghong.kotlin.beanFillNullValues
import com.github.liaochong.myexcel.core.WorkbookType
import com.github.liaochong.myexcel.core.annotation.ExcelColumn
import com.github.liaochong.myexcel.core.annotation.ExcelModel
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaType
import java.io.Serializable
import ${entityNamePackage}.*

/**
* @description jpa层代码自动生成
* @author jiangchanghong
* @date ${today}
*/
@Component
@ExcelModel(
includeAllField = false,
useFieldNameAsTitle = true,
workbookType = WorkbookType.SXLSX,
wrapText = true,
ignoreStaticFields = true,
dateFormat = "yyyy-MM-dd",
dateTimeFormat = "yyyy-MM-dd HH:mm:ss"
)
class ${entityExcelName} :Serializable {
<#list columns as column>
	@field:ExcelColumn(title = "${column.propertyName}" ,order = ${column_index})
	var ${column.propertyName}: ${column.javaTypeName}? = null

</#list>


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
other as ${entityExcelName}

<#list columns as column>
	if (${column.propertyName} != other.${column.propertyName}) return false
</#list>

return true
}

companion object {
fun newObjectFromDbEntity(db: ${entityName}): ${entityExcelName} {
val entity = ${entityExcelName}()
entity.apply {
<#list columns as column>
    ${column.propertyName} = db.${column.propertyName}
</#list>
}
return entity
}

fun newTestObject(): ${entityExcelName} {
val entity = ${entityExcelName}().apply { this.beanFillNullValues() }
for (kProperty1 in ${entityExcelName}::class.declaredMemberProperties.mapNotNull { it as? KMutableProperty<*>? }) {
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
