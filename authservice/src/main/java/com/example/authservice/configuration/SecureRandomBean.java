package com.example.authservice.configuration;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecureRandomBean {
	
	@Bean
	SecureRandom secureRandom() {
		return new SecureRandom();
	}
	
}
