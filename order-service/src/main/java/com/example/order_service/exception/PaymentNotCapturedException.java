package com.example.order_service.exception;

public class PaymentNotCapturedException extends RuntimeException{

	private static final long serialVersionUID = 7863856153807930233L;

	public PaymentNotCapturedException(String msg) {
		super(msg);
	}
	
}
