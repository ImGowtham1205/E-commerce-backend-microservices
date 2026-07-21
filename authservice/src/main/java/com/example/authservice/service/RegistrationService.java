package com.example.authservice.service;

import org.springframework.stereotype.Service;

import com.example.authservice.model.UserCache;
import com.example.authservice.model.Users;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegistrationService {
	
	private final UsersService userService;
	private final MailService mailService;
	
	public void registerUser(Users user) {
		userService.saveUser(user);
		mailService.accountCreationMail(user);
	}
	
	public boolean existsMail(String mail) {
		UserCache user = userService.getUser(mail);
		if(user != null)
			return true;
		else
			return false;
	}
	
	public boolean existsPhoneNo(String phoneno) {
		UserCache user = userService.getUserByPhoneNo(phoneno);
		if(user != null)
			return true;
		else
			return false;
	}
}
