package com.example.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.authservice.model.AdminOAuthProviders;

@Repository
public interface AdminOAuthProvidersRepo extends JpaRepository<AdminOAuthProviders, Long>{
	
	AdminOAuthProviders findByProviderAndProviderid(String provider,String providerid);
	
}
