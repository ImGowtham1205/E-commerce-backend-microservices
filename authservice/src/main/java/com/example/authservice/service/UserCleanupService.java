package com.example.authservice.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.authservice.exception.CartRecordDeletionException;
import com.example.authservice.exception.CommentRecordDeletionException;
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
		ResponseEntity<String> response = cartService.deleteUserCartItems(userid, "Bearer " + token);
		
		if(!response.getStatusCode().is2xxSuccessful()) 
			throw new CartRecordDeletionException
				("Failed to Delete Cart Item For UserID : " + userid);
		
	}
	
	@CircuitBreaker(name = "COMMENT" , fallbackMethod = "commentFallback")
	public void deleteComment(long userid, String token) {
		ResponseEntity<String> response = commentService.deleteUserComments(userid, "Bearer " + token);
		
		if(!response.getStatusCode().is2xxSuccessful()) 
			throw new CommentRecordDeletionException
				("Failed to Delete Comment Record For UserID : " + userid);
		
	}

	public void cartFallback(long userId, String token, Exception ex) {
		ex.getStackTrace();
		System.err.println("Cart Service unavailable for userId = " + userId);
	}

	public void commentFallback(long userId, String token, Exception ex) {
		ex.getStackTrace();
		System.err.println("Comment Service unavailable for userId = " + userId);
	}
	
}
