package com.example.order_service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.order_service.model.Orders;
import com.example.order_service.model.OrdersRequest;
import com.example.order_service.model.Products;
import com.example.order_service.model.UserCache;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class MailService {
	
	private final KafkaTemplate<Long, Object> kafkaTemplate;
	
	public void orderConfirmationMail(Products product, UserCache user, Orders order) {
		OrdersRequest orderRequest = new OrdersRequest(product, user, order);
		kafkaTemplate.send("order.created",order.getOrderid(),orderRequest)
			.whenComplete((result,ex) -> {
				if(ex != null)
					log.error("Failed To Send Order Created Event For Email : " + user.getEmail()
						+ " To MailService : " + ex.getMessage());
				else
					log.info("Order Created Event is Acutally Delivered For Email : " 
							+user.getEmail() + " To MailService Partition : " + result.getRecordMetadata().partition());
		});
		
	}
	
	public void orderCancellationMail(Products product, UserCache user, Orders order) {
		OrdersRequest orderRequest = new OrdersRequest(product, user, order);
		kafkaTemplate.send("order.cancelled",order.getOrderid(),orderRequest)
			.whenComplete((result,ex) -> {
				if(ex != null)
					log.error("Failed To Send Order Cancelled Event For Email : "+user.getEmail()
						+" To MailService : " + ex.getMessage());
				else
					log.info("Order Cancelled Event is Acutally Delivered For Email : "+user.getEmail()
							+" To MailService Partition : " + result.getRecordMetadata().partition());
		});
		
	}
	
}