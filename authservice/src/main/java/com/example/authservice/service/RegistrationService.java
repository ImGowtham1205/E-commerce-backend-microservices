package com.example.authservice.service;

import org.springframework.stereotype.Service;

import com.example.authservice.model.Users;
import com.example.authservice.repository.UserRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegistrationService {
	
	private final UserRepo userrepo;
	private final MailService mailservice;
	
	public void registerUser(Users user) {
		userrepo.save(user);
		mailservice.accountCreationMail(user);
	}
	
	public boolean existsMail(String mail) {
		return userrepo.existsByEmail(mail);
	}
	
	public boolean existsPhoneNo(String phoneno) {
		return userrepo.existsByPhoneno(phoneno);
	}
}
