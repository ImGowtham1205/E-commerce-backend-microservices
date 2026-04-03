package com.example.order_service.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Orders {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	private String paymentmethod;
	private String paymentid;
	private String refundid;
	private String refundstatus;
}