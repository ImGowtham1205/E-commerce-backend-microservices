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
@Table(name = "admin_password_token")
@Getter
@Setter
@NoArgsConstructor
public class AdminPasswordResetToken implements Serializable {

	private static final long serialVersionUID = 2125432123944146414L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String token;
	private LocalDateTime expirydate;
	@ManyToOne(fetch = FetchType.EAGER)
	private Admins admin;
	
	public boolean isExpiry() {
		return expirydate.isBefore(LocalDateTime.now());
	}
}
