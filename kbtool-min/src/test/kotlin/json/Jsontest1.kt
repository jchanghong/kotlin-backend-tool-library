package json

import com.jayway.jsonpath.JsonPath
import org.junit.Assert.assertTrue
import org.junit.Test

class Jsontest1 {
	@Test
	fun `测试jsonpath`() {
//        val byPath = JsonPath.read(JSONUtil.parse(jsonStr), "humanDomainList[*].certificateNumber")
		var read = JsonPath.read<List<String>>(jsonStr, "$.humanDomainList[*].certificateNumber")
		println(read)
		assertTrue(read.size == 2)
	}
}

val jsonStr = """
   
""".trimIndent()
