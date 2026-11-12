package com.example.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.comment.feign.UsersMicroService;
import com.example.comment.model.UserCache;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserMicroServiceCall {
	
	private final UsersMicroService userService;
	
	@CircuitBreaker(name = "AUTHSERVICE" , fallbackMethod = "userFallBack")
	public List<String> getAllBlackListTokens(String token){
		log.info("Request sent to Auth service to get all blacklisted tokens");
		return userService.getallBlackListTokens(token);
	}
	
	@CircuitBreaker(name = "AUTHSERVICE" , fallbackMethod = "userInfoFallBack")
	public UserCache userInfo(String token) {
		log.info("Request sent to Auth service to get user info");
		return userService.userInfo(token);
	}
	
	public List<String> userFallBack(String token,Throwable t) {
		log.warn("Auth service is down. Returning empty list of blacklisted tokens. Error: {}", t.getMessage());
		return List.of();
	}
	
	public UserCache userInfoFallBack(String token,Throwable t) {
		UserCache user = new UserCache();
		log.warn("Auth service is down. Returning empty user info. Error: {}", t.getMessage());
		return user;
	}
	
}
