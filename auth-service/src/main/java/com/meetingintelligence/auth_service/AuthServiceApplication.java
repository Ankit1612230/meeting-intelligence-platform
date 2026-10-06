package com.meetingintelligence.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class AuthServiceApplication {
	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC")); // Postgres timezone fix from your cheat sheet
		SpringApplication.run(AuthServiceApplication.class, args);
	}
}