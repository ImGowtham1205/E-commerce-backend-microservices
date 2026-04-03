package com.example.authservice.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.authservice.model.Admins;
import com.example.authservice.model.UserPrincipal;
import com.example.authservice.model.Users;
import com.example.authservice.repository.AdminRepo;
import com.example.authservice.repository.UserRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UsersService implements UserDetailsService{

	private final UserRepo userrepo;
	private final AdminRepo adminrepo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		Users user = userrepo.findByEmail(email);
		if(user != null) {
			return new UserPrincipal(user);
		}
		
		Admins admin = adminrepo.findByEmail(email);
		if(admin != null) {
			return new UserPrincipal(admin);
		}
		
		throw new UsernameNotFoundException("User Not Found");
	}
	
	public Users getUserById(long id) {
		return userrepo.findById(id);
	}
	
	public void updateUserProfile(Users user) {
		userrepo.save(user);
	}
	
	public Users getUser(String email) {
		return userrepo.findByEmail(email);
	}
	
	public Admins getAdmin(String email) {
		return adminrepo.findByEmail(email);
	}
}
