package com.vasubhakt.DevAllServiceRegistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DevAllServiceRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevAllServiceRegistryApplication.class, args);
	}

}
