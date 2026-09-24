package com.example.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.authservice.model.PasswordResetOtp;
import com.example.authservice.model.Users;

@Repository
public interface PasswordResetOtpRepo extends JpaRepository<PasswordResetOtp, Long>{

	PasswordResetOtp findByOtp(String otp);
	PasswordResetOtp findByUser(Users user);

}
