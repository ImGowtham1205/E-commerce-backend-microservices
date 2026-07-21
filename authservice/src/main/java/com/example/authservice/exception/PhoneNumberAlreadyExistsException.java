package com.example.authservice.exception;

public class PhoneNumberAlreadyExistsException extends RuntimeException {
	
	private static final long serialVersionUID = 7862030182208210979L;

	 public PhoneNumberAlreadyExistsException(String message) {
	        super(message);
	    }
}
