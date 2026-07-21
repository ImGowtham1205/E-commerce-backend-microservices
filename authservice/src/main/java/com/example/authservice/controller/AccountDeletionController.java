package com.example.authservice.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.exception.EmailNotExistsException;
import com.example.authservice.exception.PasswordNotMatchException;
import com.example.authservice.model.Admins;
import com.example.authservice.model.Users;
import com.example.authservice.service.AccountDeletionService;
import com.example.authservice.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AccountDeletionController {

	private final JwtService jwtService;
	private final AccountDeletionService accountDeletionService;

	@DeleteMapping("/api/user/accountdeletion")
	public Users userAccountDelete(HttpServletRequest request, @RequestBody Users user)
			throws EmailNotExistsException, PasswordNotMatchException {
		
		String password = user.getPassword();
		String token = jwtService.getToken(request);
		String email = jwtService.extractEmail(token);
		return accountDeletionService.userAccountDeletion(email, password, token);
	}

	@DeleteMapping("/api/admin/accountdeletion")
	public Admins adminAccountDelete(HttpServletRequest request, @RequestBody Admins admin) {
		String password = admin.getPassword();
		String token = jwtService.getToken(request);
		String email = jwtService.extractEmail(token);
		return accountDeletionService.adminAccountDeletion(email, password, token);
	}
}
