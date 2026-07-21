package com.example.authservice.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.example.authservice.exception.PhoneNumberAlreadyExistsException;
import com.example.authservice.model.AdminCache;
import com.example.authservice.model.Admins;
import com.example.authservice.model.UserCache;
import com.example.authservice.model.Users;
import com.example.authservice.repository.AdminRepo;
import com.example.authservice.repository.UserRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UsersServiceCache {
	
	private final UserRepo userRepo;
    private final AdminRepo adminRepo;
	
	@Cacheable(value = "user", key = "'user:' + #id", unless = "#result == null")
    public UserCache getUserById(long id) {
        Users user = userRepo.findById(id);
        return userCache(user);
    }

    @Cacheable(value = "user", key = "'user:' + #phoneno", unless = "#result == null")
    public UserCache getUserByPhoneNo(String phoneno) {
        Users user = userRepo.findByPhoneno(phoneno);
        return userCache(user);
    }

    @Cacheable(value = "user", key = "'user:' + #email", unless = "#result == null")
    public UserCache getUser(String email) {
        Users user = userRepo.findByEmail(email); 
        return userCache(user);
    }
     
    @Cacheable(value = "admin", key = "'admin:' + #email", unless = "#result == null")
    public AdminCache getAdmin(String email) {
        Admins admin = adminRepo.findByEmail(email); 
        return adminCache(admin);
    }

    @Caching(
        put = {
            @CachePut(value = "user", key = "'user:' + #result.id"),
            @CachePut(value = "user", key = "'user:' + #result.email"),
            @CachePut(value = "user", key = "'user:' + #result.phoneno")
        },
        evict = {
            @CacheEvict(value = "user", key = "'user:' + #user.phoneno",
            		condition = "#user.phoneno != null")
        }
    )
    public UserCache updateUserProfile(Users user) {
        Users existingUser = userRepo.findById(user.getId());

        if (existingUser != null && existingUser.getPhoneno() != null
                && !existingUser.getPhoneno().equals(user.getPhoneno())) {
            evictUserByPhone(existingUser.getPhoneno());
        }

        Users existing = userRepo.findByPhoneno(user.getPhoneno());
        if (existing != null && existing.getId() != user.getId())
            throw new PhoneNumberAlreadyExistsException("Phone number already exists for another user");

        Users updatedUser = userRepo.save(user);
        return userCache(updatedUser);
    }
    
    @CacheEvict(value = "user", key = "'user:' + #phoneno")
    public void evictUserByPhone(String phoneno) {}
    
    @Caching(
        evict = {
            @CacheEvict(value = "user", key = "'user:' + #result.id"),
            @CacheEvict(value = "user", key = "'user:' + #result.email"),
            @CacheEvict(value = "user", key = "'user:' + #result.phoneno")
        }
    )
    public Users deleteByUserEmail(String email) {
        Users user = userRepo.findByEmail(email);
        if (user == null)
            return null;
        userRepo.delete(user);
        return user;
    }
    
    @CacheEvict(value = "admin", key = "'admin:' + #result.email")
    public Admins deleteByAdminEmail(String email) {
        Admins admin = adminRepo.findByEmail(email);
        if (admin == null)
            return null;
        adminRepo.delete(admin);
        return admin;
    }
    
    public UserCache userCache(Users user) {
    	if (user == null) return null;
    	
    	return new UserCache(user.getId(), user.getName(), user.getEmail(), user.getPhoneno(), 
				user.getRole(), user.getAddress(),user.getPassword(), user.isProfileCompleted());
	}
    
    public AdminCache adminCache(Admins admin) {
    	if (admin == null) return null;
    	
    	return new AdminCache(admin.getId(), admin.getAdminName(), admin.getPhoneno(),
        		admin.getEmail(), admin.getPassword(),admin.getRole());
    }
	
}
