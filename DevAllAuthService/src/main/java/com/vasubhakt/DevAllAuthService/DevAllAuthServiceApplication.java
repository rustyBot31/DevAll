package com.vasubhakt.DevAllAuthService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableFeignClients
@EnableScheduling
@SpringBootApplication
public class DevAllAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevAllAuthServiceApplication.class, args);
	}

	@Bean
    public ExecutorService taskExecutor() {
        return Executors.newFixedThreadPool(8);
    }
}
