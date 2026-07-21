package com.example.cart_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlers {
	
	public ResponseEntity<String> handleCartItemNotFoundException(CartItemNotFoundException ex) {
		return ResponseEntity.status(404).body(ex.getMessage());
	}
	
}
