package com.example.product_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.product_service.feign.AuthMicroService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AuthMicroServiceCall {
	
	private final AuthMicroService authservice;
	
	@CircuitBreaker(name = "AUTHSERVICE" , fallbackMethod = "userFallBack")
	public List<String> getAllBlackListTokens(String token){
		log.info("Request sent to Auth service to fetch all blacklisted tokens");
		return authservice.getallBlackListTokens(token);
	}
	
	public List<String> userFallBack(String token,Throwable t) {
		log.warn("Unable To Fetch Blacklisted Tokens from Auth Service. Returning empty list. Exception: {}", t.getMessage());
		return List.of();
	}
	
}
