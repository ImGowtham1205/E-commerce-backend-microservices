package com.example.authservice.model;

import java.util.Collection;
import java.util.Collections;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails{
	
	private static final long serialVersionUID = 1L;
	
	private Users user;
	private Admins admin;
	
	public UserPrincipal(Users user){
		this.user=user;
	}
	public UserPrincipal(Admins admin) {
		this.admin = admin;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if(user != null)
			return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
		else
			return Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}
	@Override
	public @Nullable String getPassword() {		
		return user!=null ?user.getPassword() : admin.getPassword();
	}

	@Override
	public String getUsername() {
		return user != null ? user.getEmail() : admin.getEmail();
	}
}