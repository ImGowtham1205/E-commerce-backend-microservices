package com.example.authservice.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.model.Admins;
import com.example.authservice.model.Users;
import com.example.authservice.service.RegistrationService;

@RestController
@RequestMapping("/auth")
public class RegistrationController {
	
	private final RegistrationService registrationService;
	
	@Value("${admin.secret}")
	private String ADMIN_KEY;
	
	public RegistrationController(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}
	
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody Map<String, String> request) {
		
		String role = request.get("role");
		Users user = null;
		Admins admin = null;
		
		if(!role.equals("USER") && !role.equals("ADMIN"))
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role");
		
		if(role.equals("USER")) {
			
			user = registrationService.createUser(request);
			
			if(registrationService.existsMailForUser(user.getEmail()))
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
			
			if(registrationService.existsPhoneNoForUser(user.getPhoneno()))
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone number already exists");
			
			registrationService.registerUser(user);
		}
		
		else if(role.equals("ADMIN")) {
			
			String adminKey = request.get("adminkey");
			
			if(!ADMIN_KEY.equals(adminKey))
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body("Invalid Admin Key , Cannot create admin account");
			else
				admin = registrationService.createAdmin(request);
			
			if(registrationService.existsMailForAdmin(admin.getEmail()))
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
			
			if(registrationService.existsPhoneNoForAdmin(admin.getPhoneno()))
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone number already exists");
			
			registrationService.registerAdmin(admin);
		}
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(role+" Account Created Successfully");
	}
}
