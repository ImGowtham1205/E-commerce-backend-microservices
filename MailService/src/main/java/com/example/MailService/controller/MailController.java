package com.example.MailService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.MailService.model.AdminForgotPasswordRequest;
import com.example.MailService.model.Admins;
import com.example.MailService.model.OrdersRequest;
import com.example.MailService.model.UserForgotPasswordRequest;
import com.example.MailService.model.Users;
import com.example.MailService.service.MailService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class MailController {

	private final MailService mailService;

	@PostMapping("/user/sendcreationmail")
	public ResponseEntity<String> sendUserCreationMail(@RequestBody Users user) 
			throws MailException , Exception {
		mailService.accountCreationMail(user);
		return ResponseEntity.ok("User creation mail sent successfully");
	}

	@PostMapping("/user/sendforgotpasswordmail")
	public ResponseEntity<String> sendForgotPasswordMailForUser
			(@RequestBody UserForgotPasswordRequest request) throws MailException , Exception {
		mailService.forgotPasswordMail(request.user(), request.token());
		return ResponseEntity.ok("Forgot password mail sent successfully");
	}

	@PostMapping("/user/orderconfirmationmail")
	public ResponseEntity<String> orderConfirmationMail(@RequestBody OrdersRequest request) 
			throws MailException , Exception {
		mailService.orderConfirmationMail(request.product(), request.user(), request.order());
		return ResponseEntity.ok("Order confirmation mail sent successfully");
	}

	@PostMapping("/user/orderCancellationmail")
	public ResponseEntity<String> orderCancellationMail(@RequestBody OrdersRequest request) 
			throws MailException , Exception {
		mailService.orderCancellationMail(request.product(), request.user(), request.order());
		return ResponseEntity.ok("Order cancellation mail sent successfully");
	}

	@PostMapping("/user/accountdeletionmail")
	public ResponseEntity<String> userAccountDeletionMail(@RequestBody Users user) 
			throws MailException , Exception {
		mailService.userAccountDeletionMail(user);
		return ResponseEntity.ok("User account deletion mail sent successfully");
	}

	@PostMapping("/admin/accountdeletionmail")
	public ResponseEntity<String> adminAccountDeletionMail(@RequestBody Admins admin) 
			throws MailException , Exception {
		mailService.adminAccountDeletionMail(admin);
		return ResponseEntity.ok("Admin account deletion mail sent successfully");
	}

	@PostMapping("/admin/sendcreationmail")
	public ResponseEntity<String> sendAdminCreationMail(@RequestBody Admins admin) 
			throws MailException , Exception {
		mailService.adminAccountCreationMail(admin);
		return ResponseEntity.ok("Admin creation mail sent successfully");
	}

	@PostMapping("/admin/sendforgotpasswordmail")
	public ResponseEntity<String> sendForgotPasswordMailForAdmin
		(	@RequestBody AdminForgotPasswordRequest request) throws MailException , Exception {
		mailService.forgotPasswordMail(request.admin(), request.token());
		return ResponseEntity.ok("Forgot password mail sent successfully");
	}

}
