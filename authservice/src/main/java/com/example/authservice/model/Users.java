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
@Table( name ="users",
		uniqueConstraints = @UniqueConstraint(columnNames = {"email","phoneno"}),
 		indexes = {@Index(name = "idx_users_email", columnList = "email")
 				  ,@Index(name = "idx_users_phoneno", columnList = "phoneno")})

@Getter
@Setter
@NoArgsConstructor
public class Users implements Serializable{
	
	private static final long serialVersionUID = -7899218745492193170L;

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
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	
	@Column(name="role")
	private String role = "USER";
	
	@Column(name="address",length = 700)
	private String address;
	
	@Column(name = "is_profile_completed")
	private boolean profileCompleted;
	
	@OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonIgnore
	private List<PasswordResetToken> token;
	
	@OneToMany(mappedBy = "userid",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonIgnore 
	private List<OAuthProviders> oauthProviders;
}