package com.example.order_service.exception;

public class MailNotSentException extends RuntimeException {
	
	private static final long serialVersionUID = 7687735499784602883L;

	public MailNotSentException(String message) {
		super(message);
	}

}
