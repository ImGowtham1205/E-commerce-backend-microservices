package com.example.authservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.service.BlackListTokenService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class BlackListTokenController {
	
	private final BlackListTokenService tokenService;
	
	@GetMapping("/api/getallblacklisttokens")
	public List<String> getallBlackListTokens() {
		
		log.info("Request sent To service Layer for fetching all blacklisted tokens");
		return tokenService.getAllBlackListTokens();
	}
	
}
