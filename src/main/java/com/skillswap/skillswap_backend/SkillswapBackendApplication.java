package com.skillswap.skillswap_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.skillswap")
@EntityScan(basePackages = "com.skillswap.entity")
@EnableJpaRepositories(basePackages = "com.skillswap.repository")
public class SkillswapBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkillswapBackendApplication.class, args);
	}
}