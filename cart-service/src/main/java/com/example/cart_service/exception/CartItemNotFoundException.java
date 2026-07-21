package com.example.cart_service.exception;

public class CartItemNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 7417340080346604682L;

	public CartItemNotFoundException(String message) {
		super(message);
	}

}
