package com.example.order_service.exception;

public class ProductStockUpdationException extends RuntimeException{

	private static final long serialVersionUID = 773270003093279982L;
	
	public ProductStockUpdationException(String msg) {
		super(msg);
	}

}
