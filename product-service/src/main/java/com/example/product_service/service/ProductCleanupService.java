package com.example.product_service.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.product_service.exception.CartRecordDeletionException;
import com.example.product_service.exception.CommentRecordDeletionException;
import com.example.product_service.feign.CartMicroService;
import com.example.product_service.feign.CommentMicroService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductCleanupService {

	private final CartMicroService cartService;
	private final CommentMicroService commentService;

	@CircuitBreaker(name = "CART-SERVICE" , fallbackMethod = "cartFallBack")
	public void deleteCart(long productid) {
		ResponseEntity<String> response = cartService.deleteCartItemsByProductId(productid);
		
		if(!response.getStatusCode().is2xxSuccessful()) 
			throw new CartRecordDeletionException
				("Failed to Delete Cart Item For ProductID : " + productid);
		
	}
	
	@CircuitBreaker(name = "COMMENT" , fallbackMethod = "commentFallBack")
	public void deleteComment(long productid) {
		ResponseEntity<String> response = commentService.deleteCommentsByProductId(productid);
		
		if(!response.getStatusCode().is2xxSuccessful()) 
			throw new CommentRecordDeletionException
				("Failed to Delete Comment Records For ProductID : " + productid);
		
	}

	public void cartFallBack(long productid, Exception ex) {
		System.err.println("Cart Service unavailable for productId = " + productid + ", reason: " + ex.getMessage());
	}

	public void commentFallBack(long productid, Exception ex) {
		System.err.println("Comment Service unavailable for productId = " + productid + ", reason: " + ex.getMessage());
	}
	
}
