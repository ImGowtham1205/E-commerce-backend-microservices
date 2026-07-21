package com.example.product_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.product_service.feign.AuthMicroService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthMicroServiceCall {
	
	private final AuthMicroService authservice;
	
	@CircuitBreaker(name = "AUTHSERVICE" , fallbackMethod = "userFallBack")
	public List<String> getAllBlackListTokens(String token){
		System.out.println("Main Method Called");
		return authservice.getallBlackListTokens(token);
	}
	
	public List<String> userFallBack(String token,Throwable t) {
		System.out.println("Unable To Call The AUTHSERVICE At This Time");
		return List.of();
	}
	
}
