package com.vasubhakt.DevAllProjectService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DevAllProjectServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevAllProjectServiceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
    public ExecutorService taskExecutor() {
        return Executors.newFixedThreadPool(8);
    }
}
