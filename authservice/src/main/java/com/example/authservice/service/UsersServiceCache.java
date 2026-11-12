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
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
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
        log.info("Fetching User with email {} from database and caching them", email);
        return userCache(user);
    }
     
    @Cacheable(value = "admin", key = "'admin:' + #email", unless = "#result == null")
    public AdminCache getAdmin(String email) {
        Admins admin = adminRepo.findByEmail(email); 
        log.info("Fetching Admin with email {} from database and caching them", email);
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
        	log.info("Phone number changed for user with email {}. Evicting old phone number from cache.", user.getEmail());
            evictUserByPhone(existingUser.getPhoneno());
        }
        
        log.info("Fetching User with Phone Number {} from database", user.getPhoneno());
        Users existing = userRepo.findByPhoneno(user.getPhoneno());
        
        if (existing != null && existing.getId() != user.getId()) {
        	log.warn("Phone number {} already exists for another user. Cannot update profile.", user.getPhoneno());
			throw new PhoneNumberAlreadyExistsException("Phone number already exists");
        }

        Users updatedUser = userRepo.save(user);
        log.info("User with email {} updated successfully. Updating cache.", user.getEmail());
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
        if (user == null) {
        	log.warn("User with email {} not found for deletion", email);
			return null;
        }
        userRepo.delete(user);
        log.info("User with email {} deleted and cache evicted successfully", email);
        return user;
    }
    
    @Caching(
		evict = {
			@CacheEvict(value = "admin", key = "'admin:' + #result.email"),
			@CacheEvict(value = "admin", key = "'admin:' + #result.phoneno")
		}
	)
    public Admins deleteByAdminEmail(String email) {
        Admins admin = adminRepo.findByEmail(email);
        if (admin == null) {
        	log.warn("Admin with email {} not found for deletion", email);
			return null;
        }
        adminRepo.delete(admin);
        log.info("Admin with email {} deleted and cache evicted successfully", email);
        return admin;
    }
    
    @Cacheable(value = "admin", key = "'admin:' + #phoneno", unless = "#result == null")
    public AdminCache getAdminByPhoneNo(String phoneno) {
		Admins admin = adminRepo.findByPhoneno(phoneno);
		return adminCache(admin);
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
