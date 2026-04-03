package com.example.product_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.product_service.feign.AuthMicroService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TokenService {
	
	private final AuthMicroService authservice;
	
	public List<String> getAllBlackListTokens(String token){
		return authservice.getallBlackListTokens(token);
	}
	
}
