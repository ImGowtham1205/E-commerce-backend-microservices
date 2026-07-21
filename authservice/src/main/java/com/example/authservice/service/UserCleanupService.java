package com.example.authservice.service;

import org.springframework.stereotype.Service;

import com.example.authservice.feign.CartMicroService;
import com.example.authservice.feign.CommentMicroService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserCleanupService {

	private final CartMicroService cartService;
	private final CommentMicroService commentService;

	@CircuitBreaker(name = "CART-SERVICE" , fallbackMethod = "cartFallback")
	public void deleteCart(long userid, String token) {
		cartService.deleteUserCartItems(userid, "Bearer " + token);
	}
	
	@CircuitBreaker(name = "COMMENT" , fallbackMethod = "commentFallback")
	public void deleteComment(long userid, String token) {
		commentService.deleteUserComments(userid, "Bearer " + token);
	}

	public void cartFallback(long userId, String token, Exception ex) {
		System.err.println("Cart Service unavailable for userId = " + userId);
	}

	public void commentFallback(long userId, String token, Exception ex) {
		System.err.println("Comment Service unavailable for userId = " + userId);
	}
	
}
