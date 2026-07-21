package com.example.product_service.projection;

import java.io.Serializable;

public record ProductView(
	    long id,
	    String productname,
	    String description,
	    long price,
	    long stock,
	    String category
	) implements Serializable {}
