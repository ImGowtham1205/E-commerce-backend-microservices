package com.example.order_service.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Products {
	
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
