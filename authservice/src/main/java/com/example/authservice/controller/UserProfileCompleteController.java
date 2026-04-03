package com.example.authservice.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.model.Users;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class UserProfileCompleteController {

	private final JwtService jwtservice;
	private final UsersService userservice;
	private final PasswordEncoder encorder;
		
	@PutMapping("/api/user/complete-profile")
	public ResponseEntity<String> profileComplete(@RequestBody Map<String,String> profile
			,HttpServletRequest request){
		String password = profile.get("password");
		String address = profile.get("address");
		String phoneno = profile.get("phoneno");
		
		String token = jwtservice.getToken(request);
		String email = jwtservice.extractEmail(token);
		Users user = userservice.getUser(email);
		
		user.setAddress(address);
		user.setPhoneno(phoneno);
		user.setPassword(encorder.encode(password));
		user.setProfileCompleted(false);
		userservice.updateUserProfile(user);
		return ResponseEntity.ok("Your Profile Completed Successfully..");
	}
	
}
