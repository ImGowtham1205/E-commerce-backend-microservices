package com.example.authservice.service;

import org.springframework.stereotype.Service;

import com.example.authservice.model.AdminPasswordResetToken;
import com.example.authservice.model.PasswordResetToken;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PasswordTokenService {

    private final PasswordTokenCacheService cacheService;

    public PasswordResetToken checkToken(String token) {
        PasswordResetToken prt = cacheService.findByUserToken(token);
        if (prt == null || prt.isExpiry())
            return null;
        return prt;
    }

    public AdminPasswordResetToken checkAdminToken(String token) {
        AdminPasswordResetToken prt = cacheService.findAdminByToken(token);
        if (prt == null || prt.isExpiry())
            return null;
        return prt;
    }

    public PasswordResetToken savePasswordToken(PasswordResetToken prt) {
        return cacheService.savePasswordToken(prt);
    }

    public AdminPasswordResetToken saveAdminPasswordToken(AdminPasswordResetToken prt) {
        return cacheService.saveAdminPasswordToken(prt);
    }

    public void deleteUserToken(PasswordResetToken prt) {
        cacheService.deleteUserToken(prt);
    }

    public void deleteAdminToken(AdminPasswordResetToken aprt) {
        cacheService.deleteAdminToken(aprt);
    }
}