package com.example.order_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.order_service.feign.AuthMicroService;
import com.example.order_service.model.UserCache;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AuthMicroServiceCall {
	
	private final AuthMicroService authService;
	
	@CircuitBreaker(name = "AUTHSERVICE" , fallbackMethod = "userFallBack")
	public List<String> getAllBlackListTokens(String token){
		log.info("Request sent to Auth service to fetch all blacklisted tokens");
		return authService.getallBlackListTokens(token);
	}
	
	@CircuitBreaker(name = "AUTHSERVICE" , fallbackMethod = "userInfoFallBack")
	public UserCache userInfo(String token) {
		log.info("Request sent to Auth service to get user info");
		return authService.userInfo(token);
	}
	
	public List<String> userFallBack(String token,Throwable t) {
		log.warn("Unable To Fetch Blacklisted Tokens from Auth Service. Returning empty list. Exception: {}", t.getMessage());
		return List.of();
	}
	
	public UserCache userInfoFallBack(String token,Throwable t) {
		UserCache user = new UserCache();
		log.warn("Auth service is down. Returning empty user info. Error: {}", t.getMessage());
		return user;
	}
	
}
