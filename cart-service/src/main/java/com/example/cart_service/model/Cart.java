package com.example.cart_service.model;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Document(collection = "cart")
@Getter
@Setter
public class Cart implements Serializable{

	private static final long serialVersionUID = 1260681665724761398L;
	
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId id;
	@Indexed
	private long userId;
	@Indexed
	private long productId;
	private long quantity;
}
