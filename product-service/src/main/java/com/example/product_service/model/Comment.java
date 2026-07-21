package com.example.product_service.model;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Getter
@Setter
@NoArgsConstructor
public class Comment implements Serializable{
	
	private static final long serialVersionUID = 2763579234809052517L;
	
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId id;	
	private long userid;
	private String review;
	private String username;
	private long productid;
}
