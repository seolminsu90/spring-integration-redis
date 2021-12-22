package com.integration.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;

@EnableIntegration
@SpringBootApplication
public class SpringIntegrationRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringIntegrationRedisApplication.class, args);
	}

}
