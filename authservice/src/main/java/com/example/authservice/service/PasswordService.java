package com.example.authservice.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authservice.model.AdminCache;
import com.example.authservice.model.AdminPasswordResetOtp;
import com.example.authservice.model.Admins;
import com.example.authservice.model.PasswordResetOtp;
import com.example.authservice.model.UserCache;
import com.example.authservice.model.Users;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PasswordService {
	
	private final MailService mailService;
	private final PasswordEncoder encorder;
	private final UsersService userService;
	private final PasswordOtpService passwordOtpService;
	private final PasswordServiceCache passwordServiceCache;
	private final OtpGeneratorService otpGeneratorService;
	
	public ResponseEntity<String> forgotPassword(String email) {
		String otp = null;
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
			PasswordResetOtp existingOtp = passwordOtpService.getUser(user);
			
			if(existingOtp != null) {
				otp = existingOtp.getOtp();
				existingOtp.setStatus("unused");
				existingOtp.setExpiryTime(LocalDateTime.now().plusMinutes(15));
				passwordOtpService.savePasswordOtp(existingOtp);
				mailService.forgotPasswordMail(user, otp);
			}
			
			else {
				PasswordResetOtp pro = new PasswordResetOtp();
				otp = otpGeneratorService.generateOtp();
				pro.setOtp(otp);
				pro.setStatus("unused");
				pro.setUser(user);
				pro.setExpiryTime(LocalDateTime.now().plusMinutes(15));
				passwordOtpService.savePasswordOtp(pro);
				mailService.forgotPasswordMail(user, otp);
			}
		}
		
		if (admin != null) {
			AdminPasswordResetOtp existingOtp = passwordOtpService.getAdmin(admin);
			
			if(existingOtp != null) {
				otp = existingOtp.getOtp();
				existingOtp.setStatus("unused");
				existingOtp.setExpiryTime(LocalDateTime.now().plusMinutes(15));
				passwordOtpService.saveAdminPasswordOtp(existingOtp);
				mailService.forgotPasswordMail(admin, otp);
			}
			
			else {
				AdminPasswordResetOtp apro = new AdminPasswordResetOtp();
				otp = otpGeneratorService.generateOtp();
				apro.setOtp(otp);
				apro.setStatus("unused");
				apro.setAdmin(admin);
				apro.setExpiryTime(LocalDateTime.now().plusMinutes(15));
				passwordOtpService.saveAdminPasswordOtp(apro);
				mailService.forgotPasswordMail(admin, otp);
			}
			
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