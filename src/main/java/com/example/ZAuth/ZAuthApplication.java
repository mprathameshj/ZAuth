package com.example.ZAuth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class ZAuthApplication {


	public static void main(String[] args) {

		SpringApplication.run(ZAuthApplication.class, args);
	}

}
