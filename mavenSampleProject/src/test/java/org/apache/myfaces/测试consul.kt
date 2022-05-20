package org.apache.myfaces

import cn.hutool.http.HttpUtil
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.context.ApplicationContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class 测试consul {
	@LocalServerPort
	var port: Long? = null

	@Test
	internal fun testSwagger() {
		var get = HttpUtil.get("http://127.0.0.1:${port!!}/swagger-ui/index.html")
		assert(get.isNotBlank())
	}

	@Autowired
	lateinit var discoveryClient: DiscoveryClient

	@Autowired
	lateinit var applicationContext: ApplicationContext

	//    @Autowired
//    lateinit var restTemplate: RestTemplate
	@Test
	@Timeout(5000)
	internal fun 测试consul() {
		val services = discoveryClient.services
		assert(services.isNullOrEmpty() == false)
		val instances = discoveryClient.getInstances("testCommonAllApp")
		val instances2 = discoveryClient.getInstances("consul")
		assert(instances.isNullOrEmpty() == false)
	}

	@Test
	@Timeout(5000)
	internal fun 测试consul服务调用() {
		val restTemplate = applicationContext.getBean(FeignClientTest::class.java)
		val forEntity = restTemplate.hello()
		assert(forEntity == "hello")
	}
}
