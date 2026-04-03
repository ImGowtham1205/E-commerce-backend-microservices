package com.example.authservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.authservice.model.BlackListedToken;
import com.example.authservice.repository.BlackListTokenRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BlackListTokenService {
	
	private final BlackListTokenRepo tokenrepo;
	private JwtService jwtservice;
	
	public List<String> getAllBlackListTokens(){
		return tokenrepo.fetchAllTokens();
	}
	
	public void blackListToken(String token) {
		BlackListedToken b = new BlackListedToken();
		b.setToken(token);
		b.setExiprytime(jwtservice.extractExpiration(token));
		tokenrepo.save(b);
	}
}
