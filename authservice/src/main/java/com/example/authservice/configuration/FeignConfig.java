package com.example.authservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import feign.RequestInterceptor;

@Configuration
public class FeignConfig {

	@Bean
	public RequestInterceptor requestInterceptor() {
	    return requestTemplate -> {
	        var context = SecurityContextHolder.getContext();
	        
	        if (context.getAuthentication() != null) {
	            Object credentials = context.getAuthentication().getCredentials();
	              
	            if (credentials instanceof String token) {
	                requestTemplate.header("Authorization", "Bearer " + token);
	            }
	        }
	    };
	}
}
