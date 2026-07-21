package com.example.order_service.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Products implements Serializable{
	
	private static final long serialVersionUID = 8909257917435408424L;
	
	private long id;
	private String productname;	
	private String description;
	private long price;
	private long stock;
	private String imagename;
	private String imagetype;
	private byte[] imagedata;
	private String category;
	
}
