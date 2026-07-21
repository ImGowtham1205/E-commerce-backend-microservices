package com.example.authservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.authservice.exception.EmailNotExistsException;
import com.example.authservice.exception.PasswordNotMatchException;
import com.example.authservice.model.Admins;
import com.example.authservice.model.Users;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountDeletionService {

	private final UsersService userService;
	private final PasswordService passwordService;
	private final BlackListTokenService blackListTokenService;
	private final MailService mailService;
	private final UserCleanupService userCleanupService;

	@Transactional
	public Users userAccountDeletion(String email, String password, String token) {
		Users user = userService.getUserEntity(email);
		if (user == null)
			throw new EmailNotExistsException("User not found with email: " + email);
		
		if (passwordService.checkCurrentPassword(user, password)) {
			Users deletedUser = userService.deleteByUserEmail(email);
			userCleanupService.deleteCart(user.getId(), token);
			userCleanupService.deleteComment(user.getId(), token);
			blackListTokenService.blackListToken(token);
			mailService.userAccountDeletionMail(user);
			return deletedUser;
			
		} else
			throw new PasswordNotMatchException("Current Password doesn't match");
	}

	@Transactional
	public Admins adminAccountDeletion(String email, String password, String token) {
		Admins admin = userService.getAdminEntity(email);
		if (admin == null)
			throw new EmailNotExistsException("Admin not found with email: " + email);
		
		if (passwordService.checkCurrentPassword(admin, password)) {
			Admins deletedAdmin = userService.deleteByAdminEmail(email);
			blackListTokenService.blackListToken(token);
			mailService.adminAccountDeletionMail(admin);
			return deletedAdmin;
			
		} else
			throw new PasswordNotMatchException("Current Password doesn't match");
	}
}
