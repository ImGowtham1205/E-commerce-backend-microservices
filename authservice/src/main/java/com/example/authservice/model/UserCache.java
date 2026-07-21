package com.example.authservice.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserCache implements Serializable {
	
	private static final long serialVersionUID = 739398791190946165L;
	
	private Long id;
    private String name;
    private String email;
    private String phoneno;
    private String role;
    private String address;
    private String password;
    private boolean profileCompleted;
    
}