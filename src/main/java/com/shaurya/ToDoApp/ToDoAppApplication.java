package com.shaurya.ToDoApp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ToDoAppApplication {

	@Value("${spring.security.oauth2.client.registration.github.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.github.client-secret}")
	private String clientSecret;

	public static void main(String[] args) {
		SpringApplication.run(ToDoAppApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			System.out.println("Client ID: " + clientId);
			System.out.println("Client Secret: " + clientSecret);
		};
	}

}
