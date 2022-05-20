package com.jch;

import com.github.jchanghong.http.HttpHelper;
import com.jch.jpa.JpaEntity;
import com.jch.jpa.JpaRepo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

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
		assertTrue(synForObject.code()==200);
	}

	@Test
	void testjdbc() {
		template.execute("create table t1(id int)");
		template.execute("insert into t1 (id) values (1)");
		List<Map<String, Object>> maps = template.queryForList("select * from t1");
		System.out.println(maps);
		assertTrue(maps.size() > 0);
		for (Map<String, Object> map : maps) {
			log.info(map.values().toString());
		}
	}

	@Test
	void testJpa() {
		List<JpaEntity> all = jpaRepo.findAll();
	}
}
