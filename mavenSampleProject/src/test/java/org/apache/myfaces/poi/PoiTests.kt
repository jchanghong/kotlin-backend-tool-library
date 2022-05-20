package org.apache.myfaces.poi

import cn.hutool.poi.excel.ExcelUtil
import com.github.liaochong.myexcel.core.DefaultExcelBuilder
import com.github.liaochong.myexcel.core.annotation.ExcelColumn
import com.github.liaochong.myexcel.core.annotation.ExcelModel
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import java.io.File

class PoiTests {
	@Test
	internal fun hutoolexcel() {
		val excelReader = ExcelUtil.getReader(ClassPathResource("test.xlsx").inputStream)
		val readAll = excelReader.readAll()
		assert(!readAll.isNullOrEmpty())
		val writer = excelReader.writer
		val writeCellValue = writer.writeCellValue(0, 0, 4)
		val testFile = File("/test/test.xlsx")
		writeCellValue.flush(testFile)
		writeCellValue.close()
		val readCellValue = ExcelUtil.getReader(testFile).readCellValue(0, 0)
		assert("4" == readCellValue.toString())
	}

	@Test
	internal fun testmyexcel() {
		val list = listOf(org.apache.myfaces.poi.ExcelModel().apply { name = "测试" })
		val workbook = DefaultExcelBuilder.of(org.apache.myfaces.poi.ExcelModel::class.java).build(list)
		val numberOfSheets = workbook.numberOfSheets
		assert(numberOfSheets == 1)
		assert(workbook != null)
	}
}

@ExcelModel(includeAllField = true)
internal class ExcelModel {
	@ExcelColumn(title = "姓名")
	var name: String? = null
}
