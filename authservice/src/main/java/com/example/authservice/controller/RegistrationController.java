package com.example.authservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.model.Users;
import com.example.authservice.service.RegistrationService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class RegistrationController {
	
	private final PasswordEncoder encorder;
	private final RegistrationService registrationservice;
	
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody Users user) {
		user.setProfileCompleted(false);
		if(registrationservice.existsMail(user.getEmail()))
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
		
		else if(registrationservice.existsPhoneNo(user.getPhoneno()))
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone number already registered");
		
		user.setPassword(encorder.encode(user.getPassword()));
		registrationservice.registerUser(user);
		return ResponseEntity.status(HttpStatus.CREATED).body("User Account Created Successfully");
	}
}
