package com.example.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.comment.feign.UsersMicroService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TokenService {
	
	private final UsersMicroService userservice;
	
	public List<String> getAllBlackListTokens(String token){
		return userservice.getallBlackListTokens(token);
	}
	
}
