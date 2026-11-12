package com.example.MailService.EventListener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.MailService.model.AdminForgotPasswordRequest;
import com.example.MailService.model.AdminRequest;
import com.example.MailService.model.OrdersRequest;
import com.example.MailService.model.UserForgotPasswordRequest;
import com.example.MailService.model.UserRequest;
import com.example.MailService.service.MailService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Component
@Slf4j
public class MailEventListener {

	private final MailService mailService;

	@KafkaListener(topics = "user.created", groupId = "mail-service-group")
	public void sendUserCreationMail(UserRequest user) {
		mailService.accountCreationMail(user);
		log.info("User Created Mail Sent Successfully From Mail Servive For Email : " +user.getEmail());
	}

	@KafkaListener(topics = "user.forgot-password", groupId = "mail-service-group")
	public void sendForgotPasswordMailForUser(UserForgotPasswordRequest request) {
		mailService.forgotPasswordMail(request.getUser(), request.getOtp());
		log.info("User Forgot Password Mail Sent Successfully From Mail Servive For Email : " + request.getUser().getEmail());
	}

	@KafkaListener(topics = "user.deleted", groupId = "mail-service-group")
	public void userAccountDeletionMail(UserRequest user) {
		mailService.userAccountDeletionMail(user);
		log.info("User Deleted Mail Sent Successfully From Mail Servive For Email : " + user.getEmail());
	}

	@KafkaListener(topics = "admin.deleted", groupId = "mail-service-group")
	public void adminAccountDeletionMail(AdminRequest admin) {
		mailService.adminAccountDeletionMail(admin);
		log.info("Admin Deleted Mail Sent Successfully From Mail Servive For Email : " + admin.getEmail());
	}

	@KafkaListener(topics = "admin.created", groupId = "mail-service-group")
	public void sendAdminCreationMail(AdminRequest admin) {
		mailService.adminAccountCreationMail(admin);
		log.info("Admin Created Mail Sent Successfully From Mail Servive For Email : " + admin.getEmail());
	}

	@KafkaListener(topics = "admin.forgot-password", groupId = "mail-service-group")
	public void sendForgotPasswordMailForAdmin (AdminForgotPasswordRequest request) {
		mailService.forgotPasswordMail(request.getAdmin(), request.getOtp());
		log.info("Admin Forgot Password Mail Sent Successfully From Mail Servive For Email : " + request.getAdmin().getEmail());
	}
	
	@KafkaListener(topics = "order.created", groupId = "mail-service-group")
	public void orderConfirmationMail(OrdersRequest request) {
		mailService.orderConfirmationMail(request.getProduct(), request.getUser(), request.getOrder());
		log.info("Order Created Mail Sent Successfully From Mail Servive For Email : " + request.getUser().getEmail());
	}
	
	@KafkaListener(topics = "order.cancelled", groupId = "mail-service-group")
	public void orderCancellationMail(OrdersRequest request) {
		mailService.orderCancellationMail(request.getProduct(), request.getUser(), request.getOrder());
		log.info("Order Cancelled Mail Sent Successfully From Mail Servive For Email : " + request.getUser().getEmail());
	}
	
}
