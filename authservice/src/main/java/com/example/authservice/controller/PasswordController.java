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

import com.example.authservice.exception.PasswordNotMatchException;
import com.example.authservice.model.AdminPasswordResetOtp;
import com.example.authservice.model.Admins;
import com.example.authservice.model.PasswordResetOtp;
import com.example.authservice.model.Users;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.PasswordOtpService;
import com.example.authservice.service.PasswordService;
import com.example.authservice.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class PasswordController {
	
	private final PasswordService passwordService;
	private final PasswordEncoder encorder;
	private final JwtService jwtService;
	private final UsersService userService;
	private final PasswordOtpService passwordOtpService;
	
	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestBody Map<String,String> body) {
		String email = body.get("email");
		
		ResponseEntity<String> status = passwordService.forgotPassword(email);
		
		if(status.getStatusCode() == HttpStatus.NOT_FOUND) 
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(status.getBody());
	
		return ResponseEntity.status(HttpStatus.OK).body(status.getBody());
	}
	
	@PutMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody Map<String,String> body){
		String otp = body.get("otp");
		String password = body.get("password");
		PasswordResetOtp pro = null;
		AdminPasswordResetOtp apro = null;
		
		pro = passwordOtpService.checkOtp(otp);
		
		if(pro == null) {
			apro = passwordOtpService.checkAdminOtp(otp);
			if(apro == null)
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Or Expiry OTP");
		}
		
		if(pro != null) {
			Users user = pro.getUser();
			pro.setStatus("used");
			user.setPassword(encorder.encode(password));
			passwordService.changepassword(user);
			passwordOtpService.savePasswordOtp(pro);
		}
		
		if(apro != null) {
			Admins admin = apro.getAdmin();
			apro.setStatus("used");
			admin.setPassword(encorder.encode(password));
			passwordService.changepassword(admin);
			passwordOtpService.saveAdminPasswordOtp(apro);
		}
		
		return ResponseEntity.status(HttpStatus.OK).body("Password Reset Successfully");
	}
	
	@PutMapping("/api/user/changepassword")
	public ResponseEntity<String> changePassword(@RequestBody Map<String,String> body,
			HttpServletRequest request){
		
		String currentPassword = body.get("currentpassword");
		String newPassword = body.get("newpassword");
				
		String token = jwtService.getToken(request);
				
		if(token == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
					body("Unauthorized");
		
		String email = jwtService.extractEmail(token);
		
		Users user = userService.getUserEntity(email);
		
		if(!(passwordService.checkCurrentPassword(user, currentPassword)))
			throw new PasswordNotMatchException("Current Password Doesn't Match");
		
		user.setPassword(encorder.encode(newPassword));
		passwordService.changepassword(user);
		return ResponseEntity.status(HttpStatus.OK).body("Password Updated Successfully");
	}
	
	@PutMapping("/api/admin/changepassword")
	public ResponseEntity<String> changeAdminPassword(@RequestBody Map<String,String> body,
			HttpServletRequest request){
		
		String currentPassword = body.get("currentpassword");
		String newPassword = body.get("newpassword");	
		String token = jwtService.getToken(request);
		
		if(token == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
		
		String email = jwtService.extractEmail(token);
		Admins admin = userService.getAdminEntity(email);
		
		if(!(passwordService.checkCurrentPassword(admin, currentPassword)))
			throw new PasswordNotMatchException("Current Password Doesn't Match");
		
		admin.setPassword(encorder.encode(newPassword));
		passwordService.changepassword(admin);
		return ResponseEntity.status(HttpStatus.OK).body("Password Updated Successfully");
	}
}
