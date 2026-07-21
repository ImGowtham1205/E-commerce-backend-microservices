package com.example.order_service.exception;

public class OrderNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -517276205822863757L;

	public OrderNotFoundException(String message) {
		super(message);
	}

}
