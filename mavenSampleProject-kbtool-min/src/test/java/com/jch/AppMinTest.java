package com.jch;

import com.github.jchanghong.http.HttpHelper;
import com.jch.jpa.JpaEntity;
import com.jch.jpa.JpaRepo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class AppMinTest {

	@LocalServerPort
	int port;
	@Autowired
	DataSource dataSource;
	@Autowired
	JdbcTemplate template;
	@Autowired
	JpaRepo jpaRepo;

	@Test
	void testSwaggerui() {
		okhttp3.Response synForObject = HttpHelper.INSTANCE.getSynForObject("http://127.0.0.1:" + port + "/swagger-ui/index.html");
		assertEquals(200, synForObject.code());
	}

	@BeforeEach
	@Transactional
	void setUp() {
		jpaRepo.save(new JpaEntity().setName("name1"));
		int size = jpaRepo.findAll().size();
		System.out.println(size);
		assertEquals(size,1);
	}

	@Test
	void testjdbc() {
		List<JpaEntity> query = template.query("select * from t_jpa", new BeanPropertyRowMapper<JpaEntity>());
		assertEquals(query.size(),1);
	}

	@Test
	void testJpa() {
		List<JpaEntity> all = jpaRepo.findAll();
		assertEquals(all.size(),1);
	}
}
