package com.example.order_service.exception;

public class PaymentMisMatchException extends RuntimeException{

	private static final long serialVersionUID = 5796119738464665647L;
	
	public PaymentMisMatchException(String msg) {
		super(msg);
	}

}
