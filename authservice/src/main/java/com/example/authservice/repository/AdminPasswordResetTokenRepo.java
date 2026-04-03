package com.example.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.authservice.model.AdminPasswordResetToken;

@Repository
public interface AdminPasswordResetTokenRepo extends JpaRepository<AdminPasswordResetToken, Long>{
	AdminPasswordResetToken findByToken(String token);
}
