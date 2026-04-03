package com.example.authservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "password_token")
@Getter
@Setter
public class PasswordResetToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String token;
	private LocalDateTime expiryTime;
	
	@ManyToOne
	private Users user;
	
	public boolean isExpiry() {
		return expiryTime.isBefore(LocalDateTime.now());
	}
}