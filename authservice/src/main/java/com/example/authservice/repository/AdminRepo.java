package com.example.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.authservice.model.Admins;

@Repository
public interface AdminRepo extends JpaRepository<Admins, Long>{
	Admins findByEmail(String email);
	Admins deleteByEmail(String email);
	boolean existsByEmail(String email);
}
