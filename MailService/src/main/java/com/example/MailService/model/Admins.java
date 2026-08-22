package com.example.MailService.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor 
public class Admins {

	private long id;
	private String adminName;
	private String phoneno;
	private String email;
	private String password;
	private String role = "ADMIN";
	
}
