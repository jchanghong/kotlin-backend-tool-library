package org.apache.myfaces.restclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

//@org.springframework.boot.test.autoconfigure.web.client.RestClientTest()
@Slf4j
//@ImportAutoConfiguration(RedisAutoConfiguration.class)
public class RestClientTest {
	//    @Test
	void name(@Autowired ConfigurableApplicationContext applicationContext) {
		for (String name : applicationContext.getBeanDefinitionNames()) {
			final String canonicalName = applicationContext.getBean(name).getClass().getCanonicalName();
			log.info(canonicalName + ":" + name);
		}

	}
}
