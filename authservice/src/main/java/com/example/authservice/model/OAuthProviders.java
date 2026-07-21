package com.example.authservice.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "oauth_providers",
	uniqueConstraints = {@UniqueConstraint(columnNames = {"providerid"})})
@Getter
@Setter
@NoArgsConstructor
public class OAuthProviders implements Serializable{

	private static final long serialVersionUID = -5847915391559473061L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private Users userid;
	private String provider;
	private String providerid;
}
