package com.vasubhakt.DevAllCPService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@EnableAsync
@EnableRabbit
@EnableScheduling
@SpringBootApplication
public class DevAllCpServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevAllCpServiceApplication.class, args);
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
