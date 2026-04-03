package com.example.authservice.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authservice.model.AdminPasswordResetToken;
import com.example.authservice.model.Admins;
import com.example.authservice.model.PasswordResetToken;
import com.example.authservice.model.Users;
import com.example.authservice.repository.AdminPasswordResetTokenRepo;
import com.example.authservice.repository.AdminRepo;
import com.example.authservice.repository.PasswordResetTokenRepo;
import com.example.authservice.repository.UserRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PasswordService {
	
	private final UserRepo userrepo;
	private final PasswordResetTokenRepo passwordrepo;
	private final MailService mailservice;
	private final PasswordEncoder encorder;
	private final AdminRepo adminrepo;
	private final AdminPasswordResetTokenRepo adminpassrepo;
	private final UsersService userservice;
		
	public ResponseEntity<String> forgotPassword(String email) {
		String token = UUID.randomUUID().toString();
		Users user =null;
		Admins admin =null;
		user = userservice.getUser(email);
		
		if(user == null) {
			admin = userservice.getAdmin(email);
			if(admin == null)
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Please Enter Your Registered Email");
		}
		
		if(user != null ) {
			PasswordResetToken prt = new PasswordResetToken();
			prt.setToken(token);
			prt.setUser(user);
			prt.setExpiryTime(LocalDateTime.now().plusMinutes(15));
			passwordrepo.save(prt);		
			mailservice.forgotPasswordMail(user, token);
		}
		
		if(admin != null ) {
			AdminPasswordResetToken prt = new AdminPasswordResetToken();
			prt.setToken(token);
			prt.setAdmin(admin);
			prt.setExpirydate(LocalDateTime.now().plusMinutes(15));
			adminpassrepo.save(prt);
			mailservice.forgotPasswordMail(admin, token);
		}
		
		return ResponseEntity.status(HttpStatus.OK).body("Mail Sent Successfully");
	}
	
	public PasswordResetToken checkToken(String token) {
		PasswordResetToken prt = passwordrepo.findByToken(token);
		if(prt == null || prt.isExpiry())
			return null;
		return prt;
	}
	
	public AdminPasswordResetToken checkAdminToken(String token) {
		AdminPasswordResetToken prt = adminpassrepo.findByToken(token);
		if(prt == null || prt.isExipry())
			return null;
		return prt;
	}
	
	public void changepassword(Users user) {
		userrepo.save(user);
	}
	
	public void changepassword(Admins admin) {
		adminrepo.save(admin);
	}
	
	public void deleteToken(PasswordResetToken prt) {
		int id = prt.getId();
		passwordrepo.deleteById(id);
	}
	
	public void deleteToken(AdminPasswordResetToken prt) {
		long id = prt.getId();
		adminpassrepo.deleteById(id);
	}
	
	public boolean checkCurrentPassword(Users user ,String currentPassword) {
		if(encorder.matches(currentPassword, user.getPassword()))
			return true;
		else 
			return false;
	}
	
	public boolean checkCurrentPassword(Admins admin ,String currentPassword) {
		if(encorder.matches(currentPassword, admin.getPassword()))
			return true;
		else 
			return false;
	}
}
