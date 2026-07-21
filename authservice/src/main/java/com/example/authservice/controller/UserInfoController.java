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

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class UserInfoController {
	
	private final JwtService jwtService;
	private final UsersService userService;
	
	@GetMapping("/api/user/userinfo")
	public UserCache userInfo(HttpServletRequest request) {
		String token = jwtService.getToken(request);
		String email = jwtService.extractEmail(token);
		UserCache user = userService.getUser(email);
		return user;
	}
	
	@GetMapping("/api/admin/admininfo")
	public AdminCache adminInfo(HttpServletRequest request) {
		String token = jwtService.getToken(request);
		String email = jwtService.extractEmail(token);
		AdminCache admin = userService.getAdmin(email);
		return admin;
	}
}
