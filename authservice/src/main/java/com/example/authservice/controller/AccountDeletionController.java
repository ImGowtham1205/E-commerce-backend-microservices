package com.example.authservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.model.Admins;
import com.example.authservice.model.Users;
import com.example.authservice.service.AccountDeletionService;
import com.example.authservice.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AccountDeletionController {
	
	private final JwtService jwtservice;
	private final AccountDeletionService accountdeletionservice;
	
	@DeleteMapping("/api/user/accountdeletion")
	public ResponseEntity<String> userAccountDelete(HttpServletRequest request,@RequestBody Users user){
		String password = user.getPassword();
		String token = jwtservice.getToken(request);
		String email = jwtservice.extractEmail(token);
		ResponseEntity<String> status = accountdeletionservice.userAccountDeletion(email,password,token);
		return ResponseEntity.status(status.getStatusCode()).body(status.getBody());
	}
	
	@DeleteMapping("/api/admin/accountdeletion")
	public ResponseEntity<String> adminAccountDelete(HttpServletRequest request,@RequestBody Admins admin){
		String password = admin.getPassword();
		String token = jwtservice.getToken(request);
		String email = jwtservice.extractEmail(token);
		ResponseEntity<String> status = accountdeletionservice.adminAccountDeletion(email,password,token);
		return ResponseEntity.status(status.getStatusCode()).body(status.getBody());
	}
}
