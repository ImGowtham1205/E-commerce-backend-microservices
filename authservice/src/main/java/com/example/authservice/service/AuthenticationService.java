package com.example.authservice.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.stereotype.Service;

import com.example.authservice.model.Users;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthenticationService {

	private final AuthenticationManager manager;
	
	public Authentication verify(Users user) throws AuthenticationException {
		return manager.authenticate
				(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
	}
}