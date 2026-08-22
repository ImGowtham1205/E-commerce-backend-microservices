package com.example.cart_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlers {
	
	@ExceptionHandler(CartItemNotFoundException.class)
	public ResponseEntity<String> handleCartItemNotFoundException(CartItemNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> unExpectedException(Exception ex){
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
	}
	
}
