package com.example.authservice.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.model.AdminPasswordResetToken;
import com.example.authservice.model.Admins;
import com.example.authservice.model.PasswordResetToken;
import com.example.authservice.model.Users;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.PasswordService;
import com.example.authservice.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class PasswordController {
	
	private final PasswordService passwordservice;
	private final PasswordEncoder encorder;
	private final JwtService jwtservice;
	private final UsersService userservice;
	
	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestBody Map<String,String> body) {
		String email = body.get("email");
		ResponseEntity<String> status = passwordservice.forgotPassword(email);
		if(status.getStatusCode() == HttpStatus.NOT_FOUND)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(status.getBody());
		
		return ResponseEntity.status(HttpStatus.OK).body(status.getBody());
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody Map<String,String> body){
		String token = body.get("token");
		String password = body.get("password");
		PasswordResetToken prt = null;
		AdminPasswordResetToken aprt = null;
		
		prt = passwordservice.checkToken(token);
		if(prt == null) {
			aprt = passwordservice.checkAdminToken(token);
			if(aprt == null)
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid or expiry link");
		}
		
		if(prt != null) {
			Users user = prt.getUser();
			user.setPassword(encorder.encode(password));
			passwordservice.changepassword(user);
			passwordservice.deleteToken(prt);
		}
		
		if(aprt != null) {
			Admins admin = aprt.getAdmin();
			admin.setPassword(encorder.encode(password));
			passwordservice.changepassword(admin);
			passwordservice.deleteToken(aprt);
		}
		
		return ResponseEntity.status(HttpStatus.OK).body("Password reset successfully");
	}
	
	@PutMapping("/api/user/changepassword")
	public ResponseEntity<String> changePassword(@RequestBody Map<String,String> body,
			HttpServletRequest request){
		
		String currentPassword = body.get("currentpassword");
		String newPassword = body.get("newpassword");
				
		String token = jwtservice.getToken(request);
				
		if(token == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
					body("Unauthorized");
		
		String email = jwtservice.extractEmail(token);
		
		Users user = userservice.getUser(email);
		
		if(!(passwordservice.checkCurrentPassword(user, currentPassword)))
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Current password doesn't match");
		
		user.setPassword(encorder.encode(newPassword));
		passwordservice.changepassword(user);
		return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully");
	}
	
	@PutMapping("/api/admin/changepassword")
	public ResponseEntity<String> changeAdminPassword(@RequestBody Map<String,String> body,
			HttpServletRequest request){
		
		String currentPassword = body.get("currentpassword");
		String newPassword = body.get("newpassword");
				
		String token = jwtservice.getToken(request);
				
		if(token == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
					body("Unauthorized");
		
		String email = jwtservice.extractEmail(token);
		
		Admins admin = userservice.getAdmin(email);
		
		if(!(passwordservice.checkCurrentPassword(admin, currentPassword)))
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Current password doesn't match");
		
		admin.setPassword(encorder.encode(newPassword));
		passwordservice.changepassword(admin);
		return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully");
	}
}
