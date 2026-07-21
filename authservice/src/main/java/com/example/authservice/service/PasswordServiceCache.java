package com.example.authservice.service;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.example.authservice.model.AdminCache;
import com.example.authservice.model.Admins;
import com.example.authservice.model.UserCache;
import com.example.authservice.model.Users;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PasswordServiceCache {
	
	private final UsersService userService;
	private final UsersServiceCache userServiceCache;
	
	@Caching(put = {
	    	@CachePut(value = "user", key = "'user:' + #result.email"),
	    	@CachePut(value = "user", key = "'user:' + #result.id"),
	    	@CachePut(value = "user", key = "'user:' + #result.phoneno")
	})
	public UserCache changepassword(Users user) {
		Users updatedUser = userService.saveUser(user);
		return userServiceCache.userCache(updatedUser);
	}
	
	@CachePut(value = "admin", key = "'admin:' + #result.email")
	public AdminCache changepassword(Admins admin) {
		Admins updatedAdmin = userService.saveAdmin(admin);
		return userServiceCache.adminCache(updatedAdmin);
	}
	
}
