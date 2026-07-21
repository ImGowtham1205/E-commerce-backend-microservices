package com.example.authservice.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.authservice.model.AdminPasswordResetToken;
import com.example.authservice.model.PasswordResetToken;
import com.example.authservice.repository.AdminPasswordResetTokenRepo;
import com.example.authservice.repository.PasswordResetTokenRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PasswordTokenCacheService {

    private final PasswordResetTokenRepo passwordRepo;
    private final AdminPasswordResetTokenRepo adminPassRepo;

    @Cacheable(value = "passwordtoken", key = "'passwordtoken:' + #token", unless = "#result == null")
    public PasswordResetToken findByUserToken(String token) {
    	return passwordRepo.findByToken(token);
    }

    @Cacheable(value = "adminpasswordtoken", key = "'adminpasswordtoken:' + #token", 
    		unless = "#result == null")
    public AdminPasswordResetToken findAdminByToken(String token) {
        return adminPassRepo.findByToken(token);
    }

    @CachePut(value = "passwordtoken", key = "'passwordtoken:' + #prt.token")
    public PasswordResetToken savePasswordToken(PasswordResetToken prt) {
    	return passwordRepo.save(prt);
    }

    @CachePut(value = "adminpasswordtoken", key = "'adminpasswordtoken:' + #prt.token")
    public AdminPasswordResetToken saveAdminPasswordToken(AdminPasswordResetToken prt) {
        return adminPassRepo.save(prt);
    }

    @CacheEvict(value = "passwordtoken", key = "'passwordtoken:' + #prt.token")
    public void deleteUserToken(PasswordResetToken prt) {
        passwordRepo.deleteById(prt.getId());
    }

    @CacheEvict(value = "adminpasswordtoken", key = "'adminpasswordtoken:' + #aprt.token")
    public void deleteAdminToken(AdminPasswordResetToken aprt) {
        adminPassRepo.deleteById(aprt.getId());
    }
 
}
