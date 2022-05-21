package org.apache.myfaces

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@JsonTest
class SpringBootTest1 {
    @Test
    internal fun json1(@Autowired objectMapper: ObjectMapper?) {
        assert(objectMapper != null)
    }
}

@WebMvcTest
class WebTest {
    @Test
    internal fun name(@Autowired webController: WebController) {
    }
}

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class MyNonTransactionalTests {
    @Test
    internal fun name(@Autowired entityManager: EntityManager) {
    }
}

@JdbcTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class MyTransactionalTests {
    @Test
    internal fun name(@Autowired jdbcTemplate: JdbcTemplate) {
    }
}
