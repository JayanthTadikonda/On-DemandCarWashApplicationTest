package com.jay.CWPaymentService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class CwPaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CwPaymentServiceApplication.class, args);
	}

}
