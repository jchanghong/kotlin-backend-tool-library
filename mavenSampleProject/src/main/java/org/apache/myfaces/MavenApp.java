package org.apache.myfaces;

import org.apache.myfaces.lombok.LombokKt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@ConfigurationPropertiesScan
//@EnableJchRedisCacheConfig
public class MavenApp {
	@Autowired
	DataSource dataSource;

	public static void main(String[] args) {
		LombokKt.INSTANCE.test();
		SpringApplication.run(MavenApp.class, args);
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		final RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
		final RestTemplate build = restTemplateBuilder.build();
		return build;
	}
}
