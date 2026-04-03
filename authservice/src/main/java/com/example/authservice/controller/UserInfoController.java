package com.example.authservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.model.Admins;
import com.example.authservice.model.Users;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class UserInfoController {
	
	private final JwtService jwtservice;
	private final UsersService userservice;
	
	@GetMapping("/api/user/userinfo")
	public Users userInfo(HttpServletRequest request) {
		String token = jwtservice.getToken(request);
		String email = jwtservice.extractEmail(token);
		Users user = userservice.getUser(email);
		return user;
	}
	
	@GetMapping("/api/admin/admininfo")
	public Admins adminInfo(HttpServletRequest request) {
		String token = jwtservice.getToken(request);
		String email = jwtservice.extractEmail(token);
		Admins admin = userservice.getAdmin(email);
		return admin;
	}
}
