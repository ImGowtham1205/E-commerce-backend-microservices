package com.example.cart_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cart_service.feign.AuthMicroService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TokenService {
	
	private final AuthMicroService authService;
	
	@CircuitBreaker(name = "AUTHSERVICE", fallbackMethod = "userFallBack")
	public List<String> getAllBlackListTokens(String token) {
	    System.out.println("Main method called");
	    return authService.getallBlackListTokens(token);
	}

	public List<String> userFallBack(String token, Exception ex) {
	    System.out.println("Fallback executed");
	    return List.of();
	}
	
}
