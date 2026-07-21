package com.example.authservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.service.BlackListTokenService;
import com.example.authservice.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class LogoutController {
	
	private final JwtService jwtService;
	private final BlackListTokenService blackListTokenService;
	
	@PostMapping("/api/user/logout")
	public void userLogout(HttpServletRequest request) {
		String token = jwtService.getToken(request);
		blackListTokenService.blackListToken(token);
	}
	
	@PostMapping("/api/admin/logout")
	public void adminLogout(HttpServletRequest request) {
		String token = jwtService.getToken(request);
		blackListTokenService.blackListToken(token);
	}
}
