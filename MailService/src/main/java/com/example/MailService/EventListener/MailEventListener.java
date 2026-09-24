package com.example.MailService.EventListener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.MailService.model.AdminForgotPasswordRequest;
import com.example.MailService.model.Admins;
import com.example.MailService.model.OrdersRequest;
import com.example.MailService.model.UserForgotPasswordRequest;
import com.example.MailService.model.Users;
import com.example.MailService.service.MailService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class MailEventListener {

	private final MailService mailService;

	@KafkaListener(topics = "user.created", groupId = "mail-service-group")
	public void sendUserCreationMail(Users user) {
		mailService.accountCreationMail(user);
		System.out.println("User Created Mail Sent Successfully For Email : " +user.getEmail());
	}

	@KafkaListener(topics = "user.forgot-password", groupId = "mail-service-group")
	public void sendForgotPasswordMailForUser(UserForgotPasswordRequest request) {
		mailService.forgotPasswordMail(request.getUser(), request.getOtp());
		System.out.println("User Forgot Password Mail Sent Successfully For Email : " 
				+ request.getUser().getEmail());
	}

	@KafkaListener(topics = "user.deleted", groupId = "mail-service-group")
	public void userAccountDeletionMail(Users user) {
		mailService.userAccountDeletionMail(user);
		System.out.println("User Deleted Mail Sent Successfully For Email : "+user.getEmail());
	}

	@KafkaListener(topics = "admin.deleted", groupId = "mail-service-group")
	public void adminAccountDeletionMail(Admins admin) {
		mailService.adminAccountDeletionMail(admin);
		System.out.println("Admin Deleted Mail Sent Successfully For Email : "+admin.getEmail());
	}

	@KafkaListener(topics = "admin.created", groupId = "mail-service-group")
	public void sendAdminCreationMail(Admins admin) {
		mailService.adminAccountCreationMail(admin);
		System.out.println("Admin Created Mail Sent Successfully For Email : "+admin.getEmail());
	}

	@KafkaListener(topics = "admin.forgot-password", groupId = "mail-service-group")
	public void sendForgotPasswordMailForAdmin (AdminForgotPasswordRequest request) {
		mailService.forgotPasswordMail(request.getAdmin(), request.getOtp());
		System.out.println("Admin Forgot Password Mail Sent Successfully For Email : "
				+request.getAdmin().getEmail());
	}
	
	@KafkaListener(topics = "order.created", groupId = "mail-service-group")
	public void orderConfirmationMail(OrdersRequest request) {
		mailService.orderConfirmationMail(request.getProduct(), request.getUser(), request.getOrder());
		System.out.println("Order Created Mail Sent Successfully For Email : "+request.getUser().getEmail());
	}
	
	@KafkaListener(topics = "order.cancelled", groupId = "mail-service-group")
	public void orderCancellationMail(OrdersRequest request) {
		mailService.orderCancellationMail(request.getProduct(), request.getUser(), request.getOrder());
		System.out.println("Order Cancelled Mail Sent Successfully For Email : "+request.getUser().getEmail());
	}
	
}
