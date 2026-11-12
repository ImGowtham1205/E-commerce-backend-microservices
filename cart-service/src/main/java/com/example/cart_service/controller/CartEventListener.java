package com.example.cart_service.controller;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.cart_service.service.CartService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class CartEventListener {
	
	private final CartService cartService;
	
	@KafkaListener(topics = "user.deleted", groupId = "cart-service-group")
	public void deleteUserCartItems(long userid) {
		cartService.deleteUserCartItems(userid);
		log.info("User Cart Deleted Records is Successfully For UserId : " +userid);
	}
	
	@KafkaListener(topics = "product.deleted", groupId = "cart-service-group")
	public void deleteCartItemsByProductId(long productid) {
		cartService.deleteCartItemsByProductId(productid);
		log.info("Product Cart Deleted Records is Successfully For All User , "
				+ "Deleted ProductID : " +productid);
	}
	
}
