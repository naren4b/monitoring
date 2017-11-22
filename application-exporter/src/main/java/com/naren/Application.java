package com.naren;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public Application() {
		System.out.println("Test");
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}