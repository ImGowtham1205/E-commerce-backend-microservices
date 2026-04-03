package com.example.comment.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Users {
	
	private long id;
	private String name;
	private String email;
	private String phoneno;
	private String password;
	private String role = "USER";
	private String address;
	private boolean profileCompleted;
}