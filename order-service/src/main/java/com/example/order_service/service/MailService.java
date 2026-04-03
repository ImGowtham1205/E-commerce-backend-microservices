package com.example.order_service.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.order_service.model.Orders;
import com.example.order_service.model.Products;
import com.example.order_service.model.Users;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MailService {
	
	private SimpleMailMessage message;
	private JavaMailSender sender;
	
	public void orderConfirmationMail(Products product,Users user,Orders order) {
		String subject = "Order Confirmation – Your Order Has Been Successfully Placed";
		String receiver = user.getEmail();
		String body = "Dear "+user.getName()+",\r\n"
				+ "\r\n"
				+ "Thank you for shopping with AzCart. We are pleased to confirm that your order has been successfully placed and is currently being processed.\r\n"
				+ "\r\n"
				+ "Order Details:\r\n"
				+ "Order ID: "+order.getOrderid()+"\r\n"
				+ "Order Date: "+order.getOrderdate()+"\r\n"
				+ "Total Amount: ₹"+product.getPrice()+"\r\n"
				+ "\r\n"
				+ "Delivery Address:\r\n"
				+ user.getAddress()+"\r\n"
				+ "\r\n"
				+ "Item Ordered:\r\n"
				+ product.getProductname()+"\r\n"
				+ "\r\n"
				+ "You will receive further updates once your order has been shipped.\r\n"
				+ "If you have any questions or require assistance, please contact our support team at supportazcart.noreply@gmail.com.\r\n"
				+ "\r\n"
				+ "Thank you for choosing AzCart.\r\n"
				+ "\r\n"
				+ "Sincerely,\r\n"
				+ "AzCart Team\r\n";
		message.setSubject(subject);
		message.setText(body);
		message.setTo(receiver);
		sender.send(message);
	}
}
