package com.example.authservice.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.authservice.exception.MailNotSentException;
import com.example.authservice.feign.MailMicroService;
import com.example.authservice.model.AdminForgotPasswordRequest;
import com.example.authservice.model.Admins;
import com.example.authservice.model.UserForgotPasswordRequest;
import com.example.authservice.model.Users;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MailService {
	
	private final MailMicroService mailMicroService;
	
	@CircuitBreaker(name = "MailService" , fallbackMethod = "accountcreationMailFallback")
	public void accountCreationMail(Users user) {
		ResponseEntity<String> response = mailMicroService.sendUserCreationMail(user);
		
		if(!response.getStatusCode().is2xxSuccessful()) 
			throw new MailNotSentException
				("Failed to send account creation mail for user: " + user.getEmail());
	}
	
	@CircuitBreaker(name = "MailService" , fallbackMethod = "accountcreationMailFallback")
	public void accountCreationMail(Admins admin) {
		ResponseEntity<String> response = mailMicroService.sendAdminCreationMail(admin);
		
		if(!response.getStatusCode().is2xxSuccessful()) 
			throw new MailNotSentException
				("Failed to send account creation mail for admin: " + admin.getEmail());
	}
	
	@CircuitBreaker(name = "MailService" , fallbackMethod = "forgotPasswordForUserMailFallback")
	public void forgotPasswordMail(Users user,String token) {
		ResponseEntity<String> response = mailMicroService
				.sendForgotPasswordMailForUser(new UserForgotPasswordRequest(user, token));
		
		if(!response.getStatusCode().is2xxSuccessful()) 
			throw new MailNotSentException
				("Failed to send forgot password mail for user: " + user.getEmail());
	}
	
	@CircuitBreaker(name = "MailService" , fallbackMethod = "forgotPasswordForAdminMailFallback")
	public void forgotPasswordMail(Admins admin,String token) {
		ResponseEntity<String> response = mailMicroService
				.sendForgotPasswordMailForAdmin(new AdminForgotPasswordRequest(admin, token));
		
		if(!response.getStatusCode().is2xxSuccessful()) 
			throw new MailNotSentException
				("Failed to send forgot password mail for admin: " + admin.getEmail());}
	
	@CircuitBreaker(name = "MailService" , fallbackMethod = "userAccountDeletionMailFallback")
	public void userAccountDeletionMail(Users user) {
		ResponseEntity<String> response = mailMicroService.userAccountDeletionMail(user);
		
		if(!response.getStatusCode().is2xxSuccessful()) 
			throw new MailNotSentException
				("Failed to send account deletion mail for user: " + user.getEmail());
	}
	
	@CircuitBreaker(name = "MailService" , fallbackMethod = "adminAccountDeletionMailFallback")
	public void adminAccountDeletionMail(Admins admin) {
		ResponseEntity<String> response = mailMicroService.adminAccountDeletionMail(admin);
		
		if(!response.getStatusCode().is2xxSuccessful()) 
			throw new MailNotSentException
				("Failed to send account deletion mail for admin: " + admin.getEmail());
	}
	
	public void accountcreationMailFallback(Users user, Exception ex) {
		ex.printStackTrace();
		System.err.println("Mail Service unavailable for user: " + user.getEmail());
	}
	
	public void forgotPasswordForUserMailFallback(Users user, String token, Exception ex) {
		ex.printStackTrace();
		System.err.println("Mail Service unavailable for user: " + user.getEmail());
	}
	
	public void forgotPasswordForAdminMailFallback(Admins admin, String token, Exception ex) {
		ex.printStackTrace();
		System.err.println("Mail Service unavailable for admin: " + admin.getEmail());
	}
	
	public void userAccountDeletionMailFallback(Users user, Exception ex) {
		ex.printStackTrace();
		System.err.println("Mail Service unavailable for user: " + user.getEmail());
	}
	
	public void adminAccountDeletionMailFallback(Admins admin, Exception ex) {
		ex.printStackTrace();
		System.err.println("Mail Service unavailable for admin: " + admin.getEmail());
	}
	
	public void accountcreationMailFallback(Admins admin, Exception ex) {
		ex.printStackTrace();
		System.err.println("Mail Service unavailable for admin: " + admin.getEmail());
	}
	
}