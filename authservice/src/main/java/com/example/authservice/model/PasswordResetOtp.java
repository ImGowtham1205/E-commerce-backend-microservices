package com.example.authservice.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "password_token" , indexes = {
		@Index(name = "idx_password_otp", columnList = "otp"),
		@Index(name = "idx_password_user", columnList = "user_id")
	}
)
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetOtp implements Serializable{
	
	private static final long serialVersionUID = 8968300908536221151L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String otp;
	private LocalDateTime expiryTime;
	private String status;
	@OneToOne
	@JoinColumn(name = "user_id")
	private Users user;
	
	public boolean isExpiry() {
		return expiryTime.isBefore(LocalDateTime.now());
	}
}