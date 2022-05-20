package org.apache.myfaces.redis;

import com.github.jchanghong.autoconfig.EnableJchRedisCacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.myfaces.MavenAppTest;
import org.apache.myfaces.beans.Bean1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;

//@org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
@Slf4j
@EnableJchRedisCacheConfig
/** 通过*/
public class DataRedisTest extends MavenAppTest {
	@Test
	void name(@Autowired Bean1 bean1, @Autowired LettuceConnectionFactory lettuceConnectionFactory) {

	}

	@Test
	void name(@Autowired(required = true) RedisTemplate<String, Serializable> redisTemplate) {
		log.info("redis ok");
		final Bean1 bean1 = new Bean1();
		bean1.setName("hello");
		redisTemplate.opsForValue().set("test_redis", bean1);
		final Bean1 test_redis = (Bean1) redisTemplate.opsForValue().get("test_redis");
		Assertions.assertTrue(test_redis.getName().equals("hello"));
	}

	@TestConfiguration
	static class Config {
		@Bean
		public Bean1 bean1() {
			return new Bean1();
		}
	}
}
