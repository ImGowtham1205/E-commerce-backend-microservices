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
@Table(name = "admin_password_token")
@Getter
@Setter
public class AdminPasswordResetToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String token;
	private LocalDateTime expirydate;
	@ManyToOne
	private Admins admin;
	
	public boolean isExipry() {
		return expirydate.isBefore(LocalDateTime.now());
	}
}
