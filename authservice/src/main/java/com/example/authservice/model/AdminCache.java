package com.example.authservice.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AdminCache implements Serializable {
 
	private static final long serialVersionUID = 1699588068536792416L;
	
	private Long id;
    private String adminName;
    private String phoneno;
    private String email;
    private String password;
    private String role;
    
}