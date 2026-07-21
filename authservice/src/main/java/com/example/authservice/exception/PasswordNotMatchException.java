package com.example.authservice.exception;

public class PasswordNotMatchException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PasswordNotMatchException(String message) {
		super(message);
	}

}
