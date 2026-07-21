package com.example.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.comment.feign.UsersMicroService;
import com.example.comment.model.UserCache;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserMicroServiceCall {
	
	private final UsersMicroService userService;
	
	@CircuitBreaker(name = "AUTHSERVICE" , fallbackMethod = "userFallBack")
	public List<String> getAllBlackListTokens(String token){
		return userService.getallBlackListTokens(token);
	}
	
	@CircuitBreaker(name = "AUTHSERVICE" , fallbackMethod = "userInfoFallBack")
	public UserCache userInfo(String token) {
		return userService.userInfo(token);
	}
	
	public List<String> userFallBack(String token,Throwable t) {
		System.out.println("Unable To Call The AUTHSERVICE At This Time");
		return List.of();
	}
	
	public UserCache userInfoFallBack(String token,Throwable t) {
		UserCache user = new UserCache();
		System.out.println("Unable To Call The AUTHSERVICE At This Time");
		return user;
	}
	
}
