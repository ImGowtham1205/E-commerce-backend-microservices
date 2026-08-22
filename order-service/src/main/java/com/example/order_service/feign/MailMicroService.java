package com.example.order_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.order_service.model.OrdersRequest;

@FeignClient(name = "MailService")
public interface MailMicroService {
	
	@PostMapping("/user/orderconfirmationmail")
	public ResponseEntity<String> orderConfirmationMail(@RequestBody OrdersRequest request);
	
	@PostMapping("/user/orderCancellationmail")
	public ResponseEntity<String> orderCancellationMail(@RequestBody OrdersRequest request);
	
}