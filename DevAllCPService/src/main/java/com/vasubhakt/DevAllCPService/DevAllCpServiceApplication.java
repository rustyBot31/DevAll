package com.vasubhakt.DevAllCPService;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class DevAllCpServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevAllCpServiceApplication.class, args);
	}

}
