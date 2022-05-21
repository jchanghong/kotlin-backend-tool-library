package org.apache.myfaces.blank

import org.apache.myfaces.JpaEntity
import org.apache.myfaces.MyJpaReposotory
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class JpaTest {
    @Autowired
    lateinit var myJpaReposotory: MyJpaReposotory

    @Test
    @Transactional
    internal fun `测试jpa`() {
        assert(myJpaReposotory.findAll().size == 0)
        myJpaReposotory.save(
            JpaEntity().apply {
                indexCode = "code"
                tagList = ""
            }
        )
        assert(myJpaReposotory.findAll().size == 1)
    }
}
