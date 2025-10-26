package com.vasubhakt.DevAllAuthService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class DevAllAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevAllAuthServiceApplication.class, args);
	}

}
