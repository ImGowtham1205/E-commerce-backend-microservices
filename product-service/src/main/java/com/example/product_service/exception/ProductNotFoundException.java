package com.example.product_service.exception;

public class ProductNotFoundException extends RuntimeException{

	private static final long serialVersionUID = -6691876670047355859L;
	
	public ProductNotFoundException(String msg) {
		super(msg);
	}

}
