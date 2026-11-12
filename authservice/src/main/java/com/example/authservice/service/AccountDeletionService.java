package com.example.authservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.authservice.exception.EmailNotExistsException;
import com.example.authservice.exception.PasswordNotMatchException;
import com.example.authservice.model.AdminRequest;
import com.example.authservice.model.Admins;
import com.example.authservice.model.UserRequest;
import com.example.authservice.model.Users;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AccountDeletionService {

	private final UsersService userService;
	private final PasswordService passwordService;
	private final BlackListTokenService blackListTokenService;
	private final MailService mailService;
	private final UserCleanupService userCleanupService;

	@Transactional
	public Users userAccountDeletion(String email, String password, String token) {
		Users user = userService.getUserEntity(email);
		if (user == null) {
			log.warn("User with email {} does not exist", email);
			throw new EmailNotExistsException("User not found with email: " + email);
		}
			
		if (passwordService.checkCurrentPassword(user, password)) {
			userCleanupService.deleteCart(user.getId());
			userCleanupService.deleteComment(user.getId());
			Users deletedUser = userService.deleteByUserEmail(email);
			blackListTokenService.blackListToken(token);
			mailService.userAccountDeletionMail(new UserRequest(user.getId(),user.getEmail(),user.getName()));
			return deletedUser;
			
		} 
		throw new PasswordNotMatchException("Current Password Doesn't Match");	
	}

	@Transactional
	public Admins adminAccountDeletion(String email, String password, String token) {
		Admins admin = userService.getAdminEntity(email);
		if (admin == null) {
			log.warn("Admin with email {} does not exist", email);
			throw new EmailNotExistsException("Admin not found with email: " + email);
		}

		if (passwordService.checkCurrentPassword(admin, password)) {
			Admins deletedAdmin = userService.deleteByAdminEmail(email);
			blackListTokenService.blackListToken(token);
			mailService.adminAccountDeletionMail(new AdminRequest(admin.getId(),admin.getEmail(),admin.getAdminName()));
			return deletedAdmin;
			
		} else
			throw new PasswordNotMatchException("Current Password Doesn't Match");
	}
}
