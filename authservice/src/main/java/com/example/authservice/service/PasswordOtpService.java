package com.example.authservice.service;

import org.springframework.stereotype.Service;

import com.example.authservice.model.AdminPasswordResetOtp;
import com.example.authservice.model.Admins;
import com.example.authservice.model.PasswordResetOtp;
import com.example.authservice.model.Users;
import com.example.authservice.repository.AdminPasswordResetOtpRepo;
import com.example.authservice.repository.PasswordResetOtpRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PasswordOtpService {

    private final PasswordResetOtpRepo passwordResetRepo;
    private final AdminPasswordResetOtpRepo adminPasswordResetRepo;
    
    public PasswordResetOtp checkOtp(String otp) {
        PasswordResetOtp pro = findByUserOtp(otp);
       
       if(pro != null) {
    	   if(pro.isExpiry() || "used".equals(pro.getStatus()) || !pro.getOtp().equals(otp))
			   return null;
    	   else
			   return pro;
       }
        return null;
    }

    public AdminPasswordResetOtp checkAdminOtp(String otp) {
        AdminPasswordResetOtp apro = findAdminByOtp(otp);
        if(apro != null) {
			if(apro.isExpiry() || "used".equals(apro.getStatus()) || !apro.getOtp().equals(otp))
				return null;
			else
				return apro;
		}
		return null;
	}
    
    public PasswordResetOtp findByUserOtp(String otp) {
    	return passwordResetRepo.findByOtp(otp);
    }

    public AdminPasswordResetOtp findAdminByOtp(String otp) {
        return adminPasswordResetRepo.findByOtp(otp);
    }

    public PasswordResetOtp savePasswordOtp(PasswordResetOtp pro) {
    	return passwordResetRepo.save(pro);
    }

    public AdminPasswordResetOtp saveAdminPasswordOtp(AdminPasswordResetOtp apro) {
        return adminPasswordResetRepo.save(apro);
    }

    public PasswordResetOtp getUser(Users user) {
    	return passwordResetRepo.findByUser(user);
    }
    
    public AdminPasswordResetOtp getAdmin(Admins admin) {
    	return adminPasswordResetRepo.findByAdmin(admin);
    }
     
}
