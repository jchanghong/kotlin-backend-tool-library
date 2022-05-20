package org.apache.myfaces.blank

import org.apache.myfaces.MyMybatisMapper
import org.apache.myfaces.MybatisEntity
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class 测试mybatisplus {
	@Autowired
	lateinit var mybatisMapper: MyMybatisMapper

	@Test
	@Transactional
	internal fun `测试mybatisplus`() {
		assert(mybatisMapper.selectList(null).size == 0)
		mybatisMapper.insert(MybatisEntity().apply {
			indexCode = "code"
			tagList = ""
		})
		assert(mybatisMapper.selectList(null).size == 1)
	}
}
