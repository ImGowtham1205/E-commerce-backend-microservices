package com.example.authservice.service;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authservice.model.AdminCache;
import com.example.authservice.model.Admins;
import com.example.authservice.model.UserCache;
import com.example.authservice.model.Users;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegistrationService {
	
	private final UsersService userService;
	private final MailService mailService;
	private final PasswordEncoder encorder;
	
	public void registerUser(Users user) {
		userService.saveUser(user);
		mailService.accountCreationMail(user);
	}
	
	public void registerAdmin(Admins admin) {
		userService.saveAdmin(admin);
		mailService.accountCreationMail(admin);
	}
	
	public boolean existsMailForUser(String mail) {
		UserCache user = userService.getUser(mail);
		return user != null;
	}
	
	public boolean existsPhoneNoForUser(String phoneno) {
		UserCache user = userService.getUserByPhoneNo(phoneno);
		return user != null;
	}
	
	public boolean existsMailForAdmin(String mail) {
		AdminCache user = userService.getAdmin(mail);
		return user != null;
	}
	
	public boolean existsPhoneNoForAdmin(String phoneno) {
		AdminCache user = userService.getAdminByPhoneNo(phoneno);
		return user != null;
	}
	
	public Users createUser(Map<String, String> request) {
		Users user = new Users();
		user.setName(request.get("name"));
		user.setEmail(request.get("email"));
		user.setPhoneno(request.get("phoneno"));
		user.setPassword(encorder.encode(request.get("password")));
		user.setAddress(request.get("address"));
		user.setProfileCompleted(false);
		user.setRole(request.get("role"));
		return user;
	}
	
	public Admins createAdmin(Map<String, String> request) {
		Admins admin = new Admins();
		admin.setAdminName(request.get("name"));
		admin.setEmail(request.get("email"));
		admin.setPhoneno(request.get("phoneno"));
		admin.setPassword(encorder.encode(request.get("password")));
		admin.setRole(request.get("role"));
		return admin;
	}
	
}
