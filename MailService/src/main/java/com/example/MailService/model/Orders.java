package com.example.MailService.model;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Orders{

	private long orderid;
	private long productid;
	private LocalDate orderdate;
	private LocalTime ordertime;
	private String payment_Status;
	private String order_status = "NOT DELIVERED";
	private String username;
	private String address;
	private String phoneno;
	private long userid;
	private long price;
	private String paymentmethod;
	private String paymentid;
	private String refundid;
	private String refundstatus;
	
}