package com.example.authservice.exception;

public class EmailNotExistsException extends RuntimeException{

	private static final long serialVersionUID = 9826559043708688L;

	public EmailNotExistsException(String msg) {
		super(msg);
	}
	
}
