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
@Table(name ="users",uniqueConstraints = @UniqueConstraint(columnNames = {"email"}),
 		indexes = {@Index(name = "idx_users_email", columnList = "email")})
@Getter
@Setter
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="uname")
	private String name;
	
	@Column(name="email")
	private String email;
	
	@Column(name="phoneno")
	private String phoneno;
	
	@Column(name="pass")
	private String password;
	
	@Column(name="role")
	private String role = "USER";
	
	@Column(name="address",length = 700)
	private String address;
	
	@Column(name = "is_profile_completed")
	private boolean profileCompleted;
	
	@OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE,orphanRemoval = true)
	private List<PasswordResetToken> token;
	
	@OneToMany(mappedBy = "userid",cascade = CascadeType.REMOVE,orphanRemoval = true)
	private List<OAuthProviders> oauthProviders;
}