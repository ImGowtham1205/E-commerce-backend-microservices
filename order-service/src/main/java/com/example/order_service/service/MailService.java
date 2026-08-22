package com.example.order_service.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.order_service.exception.MailNotSentException;
import com.example.order_service.feign.MailMicroService;
import com.example.order_service.model.Orders;
import com.example.order_service.model.OrdersRequest;
import com.example.order_service.model.Products;
import com.example.order_service.model.UserCache;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MailService {
	
	private final MailMicroService mailMicroService;
	
	@CircuitBreaker(name = "MailService", fallbackMethod = "orderConfirmationMailFallback")
	public void orderConfirmationMail(Products product, UserCache user, Orders order) {
		ResponseEntity<String> response = mailMicroService
				.orderConfirmationMail(new OrdersRequest(product, user, order));
		
		if (!response.getStatusCode().is2xxSuccessful())
				throw new MailNotSentException
					("Failed to send order confirmation mail for : " + user.getEmail());
		
	}
	
	@CircuitBreaker(name = "MailService", fallbackMethod = "orderCancellationMailFallback")
	public void orderCancellationMail(Products product, UserCache user, Orders order) {
		ResponseEntity<String> response = mailMicroService
				.orderCancellationMail(new OrdersRequest(product, user, order));
		
		if (!response.getStatusCode().is2xxSuccessful())
				throw new MailNotSentException
					("Failed to send order cancellation mail for : " + user.getEmail());
		
	}
	
	public void orderConfirmationMailFallback(Products product, UserCache user, Orders order, Throwable t) {
		throw new MailNotSentException
			("Failed to send order confirmation mail for : " + user.getEmail() + " due to : " + t.getMessage());
	}

	public void orderCancellationMailFallback(Products product, UserCache user, Orders order, Throwable t) {
		throw new MailNotSentException
			("Failed to send order cancellation mail for : " + user.getEmail() + " due to : " + t.getMessage());
	}
	
}