package com.example.authservice.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "admins",uniqueConstraints = @UniqueConstraint(columnNames = {"email","phoneno"}),
		indexes = {@Index(name = "idx_admins_email", columnList = "email")})
@Getter
@Setter
public class Admins {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "aname",nullable = false)
	private String adminName;
	
	@Column(nullable = false)
	private String phoneno;
	
	@Column(nullable = false)
	private String email;
	
	@Column(name = "pass",nullable = false)
	private String password;
	
	@Column(nullable = false)
	private String role = "ADMIN";
	
	@OneToMany(mappedBy = "admin",cascade = CascadeType.REMOVE,orphanRemoval = true)
	private List<AdminPasswordResetToken> token;
}
