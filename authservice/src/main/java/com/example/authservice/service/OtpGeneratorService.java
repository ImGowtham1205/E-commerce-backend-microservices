package com.example.authservice.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OtpGeneratorService {
	
	private final SecureRandom secureRandom;
	
	public String generateOtp() {
		int otp = secureRandom.nextInt(1000000);
		return String.format("%06d", otp);
	}
	
	
}
