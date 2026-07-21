package com.example.authservice.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authservice.model.AdminCache;
import com.example.authservice.model.AdminPasswordResetToken;
import com.example.authservice.model.Admins;
import com.example.authservice.model.PasswordResetToken;
import com.example.authservice.model.UserCache;
import com.example.authservice.model.Users;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PasswordService {
	
	private final MailService mailService;
	private final PasswordEncoder encorder;
	private final UsersService userService;
	private final PasswordTokenService passwordTokenService;
	private final PasswordServiceCache passwordServiceCache;
	
	public ResponseEntity<String> forgotPassword(String email) {
		String token = UUID.randomUUID().toString();
		Users user = null;
		Admins admin = null;
		user = userService.getUserEntity(email);
		
		if (user == null) {
			admin = userService.getAdminEntity(email);
			if (admin == null) 
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Please Enter Your Registered Email");
			
		}
		
		if (user != null) {
			PasswordResetToken prt = new PasswordResetToken();
			prt.setToken(token);
			prt.setUser(user);
			prt.setExpiryTime(LocalDateTime.now().plusMinutes(15));
			passwordTokenService.savePasswordToken(prt);
			mailService.forgotPasswordMail(user, token);
		}
		
		if (admin != null) {
			AdminPasswordResetToken aprt = new AdminPasswordResetToken();
			aprt.setToken(token);
			aprt.setAdmin(admin);
			aprt.setExpirydate(LocalDateTime.now().plusMinutes(15));
			passwordTokenService.saveAdminPasswordToken(aprt);
			mailService.forgotPasswordMail(admin, token);
		}
		return ResponseEntity.status(HttpStatus.OK).body("Mail Sent Successfully");
	}

	public boolean checkCurrentPassword(Users user, String currentPassword) {
		if (encorder.matches(currentPassword, user.getPassword()))
			return true;
		else
			return false;
	}

	public boolean checkCurrentPassword(Admins admin, String currentPassword) {
		if (encorder.matches(currentPassword, admin.getPassword()))
			return true;
		else
			return false;
	}
	
	public UserCache changepassword(Users user) {
		return passwordServiceCache.changepassword(user);
	}

	public AdminCache changepassword(Admins admin) {
		return passwordServiceCache.changepassword(admin);
	}
	
}