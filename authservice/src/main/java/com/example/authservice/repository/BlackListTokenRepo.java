package com.example.authservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.authservice.model.BlackListedToken;

public interface BlackListTokenRepo extends JpaRepository<BlackListedToken, Long>{
	boolean existsByToken(String token);
	@Query("select b.token as token from BlackListedToken b")
	List<String> fetchAllTokens();
}
