package com.example.authservice.exception;

public class CartRecordDeletionException extends RuntimeException{

	private static final long serialVersionUID = 6364359130674702241L;
	
	public CartRecordDeletionException(String msg) {
		super(msg);
	}

}
