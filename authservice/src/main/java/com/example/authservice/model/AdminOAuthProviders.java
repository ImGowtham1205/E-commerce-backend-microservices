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
@Table(name = "Admin_oauth_providers",
	uniqueConstraints = {@UniqueConstraint(columnNames = {"providerid"})})
@Getter
@Setter
@NoArgsConstructor
public class AdminOAuthProviders implements Serializable{
	
	private static final long serialVersionUID = 2364471488411935502L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "admin_id")
	@JsonIgnore
	private Admins adminid;
	private String provider;
	private String providerid;
	
}
