package com.project.soul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SoulApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoulApplication.class, args);
	}

}
