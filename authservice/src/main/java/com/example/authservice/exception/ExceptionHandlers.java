package com.example.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlers {

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<String> loginExceptionHandler(){
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body("Incorrect mail id or password");
	}
	
	@ExceptionHandler(PhoneNumberAlreadyExistsException.class)
	public ResponseEntity<String> userPhonenoExceptionHandler(PhoneNumberAlreadyExistsException ex){
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ex.getMessage());
	}
	
	@ExceptionHandler(EmailNotExistsException.class)
	public ResponseEntity<String> userEmailExceptionHandler(EmailNotExistsException ex){
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ex.getMessage());
	}
	
	@ExceptionHandler(PasswordNotMatchException.class)
	public ResponseEntity<String> passwordNotMatchExceptionHandler(PasswordNotMatchException ex){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ex.getMessage());
	}
	
	@ExceptionHandler(CartRecordDeletionException.class)
	public ResponseEntity<String> CartRecordDeletionExceptionHandler(CartRecordDeletionException ex){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ex.getMessage());
	}
	
	@ExceptionHandler(CommentRecordDeletionException.class)
	public ResponseEntity<String> CommentRecordDeletionExceptionHandler(CommentRecordDeletionException ex){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ex.getMessage());
	}
	
}
