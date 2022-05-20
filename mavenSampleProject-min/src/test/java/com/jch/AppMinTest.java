package com.jch;

import cn.hutool.json.JSONUtil;
import com.github.jchanghong.http.HttpHelper;
import com.jch.jpa.JpaEntity;
import com.jch.jpa.JpaRepository;
import com.jch.mybatisplus.MybatisEntity;
import com.jch.mybatisplus.MybatisRepository;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试启动完成，swagger接口可用，jpa可用
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class AppMinTest {

	@LocalServerPort
	int port;
	@Autowired
	JdbcTemplate template;
	@Autowired
	JpaRepository jpaRepository;
	@Autowired
	MybatisRepository mybatisRepository;

	@Test
	void testSpringMVC() throws ExecutionException, InterruptedException, IOException {
		String url = "http://127.0.0.1:" + port + "/mvc/post?id=1";
		Response postJsonStringSyn = HttpHelper.INSTANCE.postJsonStringAsyn(url, JSONUtil.toJsonStr(new JpaEntity().setName("name1"))).get();
		System.out.println(postJsonStringSyn.code());
		String bodyString = postJsonStringSyn.body().string();
		System.out.println(bodyString);
		assertTrue(bodyString.contains("ConstraintViolationException"));
//		bean
		url = "http://127.0.0.1:" + port + "/mvc/post?id=2";
		postJsonStringSyn = HttpHelper.INSTANCE.postJsonStringAsyn(url, JSONUtil.toJsonStr(new JpaEntity())).get();
		System.out.println(postJsonStringSyn.code());
		bodyString = postJsonStringSyn.body().string();
		System.out.println(bodyString);
		assertTrue(bodyString.contains("name"));
	}

	@Test
	void testMybatis() {
		List<MybatisEntity> mybatisEntities = mybatisRepository.selectList(null);
		assertFalse(mybatisEntities.isEmpty());
		for (MybatisEntity mybatisEntity : mybatisEntities) {
			assertNotNull(mybatisEntity.getId());
			System.out.println(JSONUtil.toJsonStr(mybatisEntity));
		}
	}

	@Test
	void testSwaggerUi() {
		okhttp3.Response synForObject = HttpHelper.INSTANCE.getSynForObject("http://127.0.0.1:" + port + "/swagger-ui/index.html");
		assertEquals(200, synForObject.code());
	}

	@BeforeEach
	void setUp() {
		jpaRepository.save(new JpaEntity().setName("name1"));
		mybatisRepository.insert(new MybatisEntity().setName("name1"));
	}

	@Test
	void testjdbc() {
		List<Map<String, Object>> query = template.query("select * from t_jpa", new ColumnMapRowMapper());
		assertFalse(query.isEmpty());
		for (Map<String, Object> jpaEntity : query) {
			System.out.println(JSONUtil.toJsonStr(jpaEntity));
		}
	}

	@Test
	@Transactional
	void testJpa() {
		List<JpaEntity> all = jpaRepository.findAll();
		assertFalse(all.isEmpty());
		for (JpaEntity jpa : all) {
			jpa.setName("name2");
			assertNotNull(jpa.getId());
			System.out.println(JSONUtil.toJsonStr(jpa));
		}
		jpaRepository.saveAllAndFlush(all);
	}
}
