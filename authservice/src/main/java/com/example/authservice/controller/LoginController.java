package com.example.authservice.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.model.AdminCache;
import com.example.authservice.model.UserCache;
import com.example.authservice.model.Users;
import com.example.authservice.service.AuthenticationService;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.UsersService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class LoginController {

	private final AuthenticationService authService;
	private final JwtService jwtService;
	private final UsersService userService;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Users user) throws AuthenticationException {
		
		Authentication auth = authService.verify(user);
		if (auth.isAuthenticated()) {
			UserDetails userdetails = (UserDetails) auth.getPrincipal();
			String email = user.getEmail();
			String role = userdetails.getAuthorities().iterator().next().getAuthority();
			
			long userid = 0;
			if(role.equals("ROLE_USER")) {
				UserCache dbuser = userService.getUser(email);
				userid = dbuser.getId();
			}
			else if(role.equals("ROLE_ADMIN")) {
				AdminCache dbuser = userService.getAdmin(email);
				userid = dbuser.getId();
			}	
			
			String token = jwtService.generateToken(email, role ,userid);
			return ResponseEntity.ok(Map.of("token", token, "role", role));
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("message", "Invalid email or password"));
	}
}
