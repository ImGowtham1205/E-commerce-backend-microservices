package com.example.authservice.controller;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.exception.PhoneNumberAlreadyExistsException;
import com.example.authservice.model.UserCache;
import com.example.authservice.model.Users;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class UserProfileCompleteController {

	private final JwtService jwtService;
	private final UsersService userService;
	private final PasswordEncoder encorder;
		
	@PutMapping("/api/user/complete-profile")
	public UserCache profileComplete(@RequestBody Map<String,String> profile
			,HttpServletRequest request) throws PhoneNumberAlreadyExistsException{
		String password = profile.get("password");
		String address = profile.get("address");
		String phoneno = profile.get("phoneno");
		
		String token = jwtService.getToken(request);
		String email = jwtService.extractEmail(token);
		Users user = userService.getUserEntity(email);
		
		user.setAddress(address);
		user.setPhoneno(phoneno);
		user.setPassword(encorder.encode(password));
		user.setProfileCompleted(false);
		return userService.updateUserProfile(user);
	}
	
}
