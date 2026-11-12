package com.example.cart_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cart_service.feign.AuthMicroService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class TokenService {
	
	private final AuthMicroService authService;
	
	@CircuitBreaker(name = "AUTHSERVICE", fallbackMethod = "userFallBack")
	public List<String> getAllBlackListTokens(String token) {
		log.info("Request sent to Auth service to fetch all blacklisted tokens");
	    return authService.getallBlackListTokens(token);
	}

	public List<String> userFallBack(String token, Exception ex) {
	    log.warn("Unable To Fetch Blacklisted Tokens from Auth Service. Returning empty list. Exception: {}", ex.getMessage());
	    return List.of();
	}
	
}
