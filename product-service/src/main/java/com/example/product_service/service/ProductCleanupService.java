package com.example.product_service.service;

import org.springframework.stereotype.Service;

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
		cartService.deleteCartItemsByProductId(productid);
	}
	
	@CircuitBreaker(name = "COMMENT" , fallbackMethod = "commentFallBack")
	public void deleteComment(long productid) {
		commentService.deleteCommentsByProductId(productid);
	}

	public void cartFallBack(long productid, Throwable t) {
		System.out.println("Cart Service unavailable for productId = " + productid + ", reason: " + t.getMessage());
	}

	public void commentFallBack(long productid, Throwable t) {
		System.out.println("Comment Service unavailable for productId = " + productid + ", reason: " + t.getMessage());
	}
	
}
