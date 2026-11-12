package com.example.authservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.model.AdminCache;
import com.example.authservice.model.UserCache;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class HomeController {
	
	private final JwtService jwtService;
	private final UsersService userService;
	
	@GetMapping("/api/user/home")
	public String userGreet(HttpServletRequest request) {
		String token = jwtService.getToken(request);
		
		if(token == null) {
			log.error("Token is null");
			return null;
		}
		
		String email = jwtService.extractEmail(token);
		log.info("Request sent to service layer for user home page: " + email);
		
		UserCache user = userService.getUser(email);
		return "Welcome , "+user.getName();
	}
	
	@GetMapping("/api/admin/home")
	public String adminGreet(HttpServletRequest request) {
		String token = jwtService.getToken(request);
		if(token == null) {
			log.error("Token is null");
			return null;
		}
		
		String email = jwtService.extractEmail(token);
		log.info("Request sent to service layer for Admin home page: " + email);
		
		AdminCache admin = userService.getAdmin(email);
		return "Welcome , "+admin.getAdminName();
	}
}	