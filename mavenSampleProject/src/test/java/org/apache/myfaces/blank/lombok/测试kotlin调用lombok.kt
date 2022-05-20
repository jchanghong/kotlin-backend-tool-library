package org.apache.myfaces.blank.lombok

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class 测试kotlin调用lombok {

	@Test
	internal fun `测试kotlin调用lombok`() {
		val java1 = Java1()
		assert(java1.name.isNullOrBlank() == false)
	}
}
