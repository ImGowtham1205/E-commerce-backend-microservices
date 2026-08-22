package com.example.MailService.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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