package com.example.order_service.exception;

public class OrderAlreadyCancelledException extends RuntimeException{

	private static final long serialVersionUID = -9152128777545288164L;
	
	public OrderAlreadyCancelledException(String msg) {
		super(msg);
	}
	
}
