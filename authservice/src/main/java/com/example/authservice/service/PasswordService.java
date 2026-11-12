package com.example.authservice.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authservice.model.AdminCache;
import com.example.authservice.model.AdminPasswordResetOtp;
import com.example.authservice.model.AdminRequest;
import com.example.authservice.model.Admins;
import com.example.authservice.model.PasswordResetOtp;
import com.example.authservice.model.UserCache;
import com.example.authservice.model.UserRequest;
import com.example.authservice.model.Users;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
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

		log.info("Fetching user for email : {}", email);
		user = userService.getUserEntity(email);

		if (user == null) {
			{
				log.info("user not found for email : {}", email);
				log.info("Fetching admin for email : {}", email);
				admin = userService.getAdminEntity(email);
			}
			if (admin == null) {
				log.info("{} not found in both user and admin tables", email);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Please Enter Your Registered Email");
			}
		}

		if (user != null) {
			log.info("Checking if user has already requested for OTP");
			PasswordResetOtp existingOtp = passwordOtpService.getUser(user);

			if (existingOtp != null) {
				log.info("Existing OTP found for user: {}", email);
				otp = existingOtp.getOtp();
				existingOtp.setStatus("unused");
				existingOtp.setExpiryTime(LocalDateTime.now().plusMinutes(15));
				log.info("Updating existing OTP status for user: {}", email);
				passwordOtpService.savePasswordOtp(existingOtp);
				log.info("Sending existing OTP to user email : {}", email);
				mailService.forgotPasswordMail(new UserRequest(user.getId(),user.getEmail(),user.getName()), otp);
			}

			else {
				PasswordResetOtp pro = new PasswordResetOtp();
				log.info("Generating new OTP for user: {}.", email);
				otp = otpGeneratorService.generateOtp();
				pro.setOtp(otp);
				pro.setStatus("unused");
				pro.setUser(user);
				pro.setExpiryTime(LocalDateTime.now().plusMinutes(15));
				log.info("Saving new OTP for user: {}.", email);
				passwordOtpService.savePasswordOtp(pro);
				log.info("Sending new OTP to user email : {}", email);
				mailService.forgotPasswordMail(new UserRequest(user.getId(),user.getEmail(),user.getName()), otp);
			}
		}

		if (admin != null) {
			log.info("Checking if Admin has already requested for OTP");
			AdminPasswordResetOtp existingOtp = passwordOtpService.getAdmin(admin);

			if (existingOtp != null) {
				log.info("Existing OTP found for admin: {}", email);
				otp = existingOtp.getOtp();
				existingOtp.setStatus("unused");
				existingOtp.setExpiryTime(LocalDateTime.now().plusMinutes(15));
				log.info("Updating existing OTP status for admin: {}", email);
				passwordOtpService.saveAdminPasswordOtp(existingOtp);
				log.info("Sending existing OTP to admin email : {}", email);
				mailService.forgotPasswordMail(new AdminRequest(admin.getId(),admin.getEmail(),admin.getAdminName()), otp);
			}

			else {
				AdminPasswordResetOtp apro = new AdminPasswordResetOtp();
				log.info("Generating new OTP for admin: {}.", email);
				otp = otpGeneratorService.generateOtp();
				apro.setOtp(otp);
				apro.setStatus("unused");
				apro.setAdmin(admin);
				apro.setExpiryTime(LocalDateTime.now().plusMinutes(15));
				log.info("Saving new OTP for admin: {}.", email);
				passwordOtpService.saveAdminPasswordOtp(apro);
				log.info("Sending new OTP to admin email : {}", email);
				mailService.forgotPasswordMail(new AdminRequest(admin.getId(),admin.getEmail(),admin.getAdminName()), otp);
			}

		}
		return ResponseEntity.status(HttpStatus.OK).body("Mail Sent Successfully");
	}

	public boolean checkCurrentPassword(Users user, String currentPassword) {
		if (encorder.matches(currentPassword, user.getPassword())) {
			log.info("Current password matches for user: " + user.getEmail());
			return true;
		}
		log.info("Current password doesn't matches for user: " + user.getEmail());
		return false;
	}

	public boolean checkCurrentPassword(Admins admin, String currentPassword) {
		if (encorder.matches(currentPassword, admin.getPassword())) {
			log.info("Current password matches for admin: " + admin.getEmail());
			return true;
		}
		log.info("Current password doesn't matches for admin: " + admin.getEmail());
		return false;
	}

	public UserCache changepassword(Users user) {
		log.info("Password changed successfully for user: {}", user.getEmail());
		return passwordServiceCache.changepassword(user);
	}

	public AdminCache changepassword(Admins admin) {
		log.info("Password changed successfully for admin: {}", admin.getEmail());
		return passwordServiceCache.changepassword(admin);
	}

}