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
@Table(name = "admin_password_token" , indexes = {
		@Index(name = "idx_admin_password_otp", columnList = "otp"),
		@Index(name = "idx_password_admin", columnList = "admin_id")
	}
)
@Getter
@Setter
@NoArgsConstructor
public class AdminPasswordResetOtp implements Serializable {

	private static final long serialVersionUID = 2125432123944146414L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String otp;
	private String status;
	private LocalDateTime expiryTime;
	@OneToOne
	@JoinColumn(name = "admin_id")
	private Admins admin;
	
	public boolean isExpiry() {
		return expiryTime.isBefore(LocalDateTime.now());
	}
}
