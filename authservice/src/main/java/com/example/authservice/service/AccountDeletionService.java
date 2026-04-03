package com.example.authservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.authservice.feign.CartMicroService;
import com.example.authservice.feign.CommentMicroService;
import com.example.authservice.model.Admins;
import com.example.authservice.model.Users;
import com.example.authservice.repository.AdminRepo;
import com.example.authservice.repository.UserRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountDeletionService {
	
	private final UserRepo userrepo;
	private final AdminRepo adminrepo;
	private final PasswordService passwordservice;
	private final BlackListTokenService blacklisttokenservice;
	private final MailService mailsevice;
	private final CartMicroService cartservice;
	private final CommentMicroService commentservice;
	
	@Transactional
	public ResponseEntity<String> userAccountDeletion(String email,String password,
			String token){
		if(!userrepo.existsByEmail(email)) 
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		Users user = userrepo.findByEmail(email);
		if(passwordservice.checkCurrentPassword(user, password)) {
			userrepo.deleteByEmail(email);
			UserCleanUp(user.getId(),token);
			blacklisttokenservice.blackListToken(token);
			mailsevice.userAccountDeletionMail(user);
			return ResponseEntity.ok("Account Deleted Successfully");
		}
		else 
			return ResponseEntity.badRequest().body("Current Password doesn't match");
	}
	
	@Transactional
	public ResponseEntity<String> adminAccountDeletion(String email,String password,String token){
		if(!adminrepo.existsByEmail(email))
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
		Admins admin =adminrepo.findByEmail(email);
		
		if(passwordservice.checkCurrentPassword(admin, password)) {
			adminrepo.deleteByEmail(email);
			blacklisttokenservice.blackListToken(token);
			mailsevice.adminAccountDeletionMail(admin);
			return ResponseEntity.ok("Account Deleted Successfully");
		}
		else 
			return ResponseEntity.badRequest().body("Current Password doesn't match");
	}
	
	private void UserCleanUp(long userid,String token) {
	    try {
	        cartservice.deleteUserCartItems(userid,"Bearer "+token);
	    } catch (Exception e) {
	        System.err.println("Cart cleanup failed for userId: "+ userid);
	        e.printStackTrace();
	    }

	    try {
	        commentservice.deleteUserComments(userid,"Bearer "+token);
	    } catch (Exception e) {
	        System.err.println("Comment cleanup failed for userId: "+ userid);
	        e.printStackTrace();
	    }
	}
	
}
