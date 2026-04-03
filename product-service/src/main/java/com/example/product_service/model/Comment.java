package com.example.product_service.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Getter
@Setter
public class Comment {
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId id;	
	private long userid;
	private String review;
	private String username;
	private long productid;
}
