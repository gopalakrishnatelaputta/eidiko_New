package com.eidiko.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EidikoEmployeeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EidikoEmployeeServiceApplication.class, args);
	}

}
