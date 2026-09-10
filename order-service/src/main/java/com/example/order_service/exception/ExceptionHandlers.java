package com.example.order_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlers {

	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<String> orderNotFoundExceptionHandler(OrderNotFoundException ex){
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ex.getMessage());
	}
	
	@ExceptionHandler(OrderAlreadyCancelledException.class)
	public ResponseEntity<String> OrderAlreadyCancelledExceptionHandler(OrderAlreadyCancelledException ex){
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
	}
	
	@ExceptionHandler(PaymentNotCapturedException.class)
	public ResponseEntity<String> PaymentNotCapturedExceptionHandler(PaymentNotCapturedException ex){
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
	}
	
	@ExceptionHandler(PaymentMisMatchException.class)
	public ResponseEntity<String> PaymentMisMatchExceptionHandler(PaymentMisMatchException ex){
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
	}
	
}
