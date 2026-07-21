package com.example.authservice.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "admins",uniqueConstraints = @UniqueConstraint(columnNames = {"email","phoneno"}),
		indexes = {@Index(name = "idx_admins_email", columnList = "email")})
@Getter
@Setter
@NoArgsConstructor 
public class Admins implements Serializable {
	
	private static final long serialVersionUID = -2300291996995285721L;

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
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	
	@Column(nullable = false)
	private String role = "ADMIN";
	
	@OneToMany(mappedBy = "admin",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonIgnore 
	private List<AdminPasswordResetToken> token;
}
