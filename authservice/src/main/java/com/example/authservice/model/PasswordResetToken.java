package com.example.authservice.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "password_token")
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken implements Serializable{
	
	private static final long serialVersionUID = 8968300908536221151L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String token;
	private LocalDateTime expiryTime;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Users user;
	
	public boolean isExpiry() {
		return expiryTime.isBefore(LocalDateTime.now());
	}
}