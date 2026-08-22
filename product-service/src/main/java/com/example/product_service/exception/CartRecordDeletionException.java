package com.example.product_service.exception;

public class CartRecordDeletionException extends RuntimeException{

	private static final long serialVersionUID = 4377353509492063000L;

	public CartRecordDeletionException(String msg) {
		super(msg);
	}

}
