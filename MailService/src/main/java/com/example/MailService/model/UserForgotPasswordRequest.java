package com.example.MailService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserForgotPasswordRequest {
	
    private UserRequest user;
    private String otp;
    
}