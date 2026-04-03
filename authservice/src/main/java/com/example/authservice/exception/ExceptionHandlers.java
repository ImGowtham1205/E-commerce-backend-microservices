package com.example.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlers {

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<String> loginException(){
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body("Incorrect mail id or password");
	}
}
