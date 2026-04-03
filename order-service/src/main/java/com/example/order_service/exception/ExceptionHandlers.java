package com.example.order_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.razorpay.RazorpayException;

@RestControllerAdvice
public class ExceptionHandlers {

	@ExceptionHandler(RazorpayException.class)
	public ResponseEntity<String> paymentException(){
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("razorpay service not available currently...");
	}
}
