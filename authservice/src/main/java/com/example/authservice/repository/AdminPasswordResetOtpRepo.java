package com.example.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.authservice.model.AdminPasswordResetOtp;
import com.example.authservice.model.Admins;

@Repository
public interface AdminPasswordResetOtpRepo extends JpaRepository<AdminPasswordResetOtp, Long>{
	
	AdminPasswordResetOtp findByOtp(String otp);
	AdminPasswordResetOtp findByAdmin(Admins admin);
}
