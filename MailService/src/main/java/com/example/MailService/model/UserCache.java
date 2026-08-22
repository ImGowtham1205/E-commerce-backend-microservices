package com.example.MailService.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserCache{
	
	private Long id;
    private String name;
    private String email;
    private String phoneno;
    private String role = "USER";
    private String address;
    private String password;
    private boolean profileCompleted;
    
}