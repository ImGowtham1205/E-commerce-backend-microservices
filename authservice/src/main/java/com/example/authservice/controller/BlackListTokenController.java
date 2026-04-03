package com.example.authservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.service.BlackListTokenService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class BlackListTokenController {
	
	private final BlackListTokenService tokenservice;
	
	@GetMapping("/api/getallblacklisttokens")
	public List<String> getallBlackListTokens(){
		return tokenservice.getAllBlackListTokens();
	}
	
}
