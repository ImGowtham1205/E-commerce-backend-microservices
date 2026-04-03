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
public class HomeController {
	
	private final JwtService jwtservice;
	private final UsersService userservice;
	
	@GetMapping("/api/user/home")
	public String userGreet(HttpServletRequest request) {
		String token = jwtservice.getToken(request);
		if(token == null)
			return null;
		String email = jwtservice.extractEmail(token);
		Users user = userservice.getUser(email);
		return "Welcome , "+user.getName();
	}
	
	@GetMapping("/api/admin/home")
	public String adminGreet(HttpServletRequest request) {
		String token = jwtservice.getToken(request);
		if(token == null)
			return null;
		String email = jwtservice.extractEmail(token);
		Admins admin = userservice.getAdmin(email);
		return "Welcome , "+admin.getAdminName();
	}
}	