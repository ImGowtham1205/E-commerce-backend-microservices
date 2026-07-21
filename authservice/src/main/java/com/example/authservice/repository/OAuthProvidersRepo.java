package com.example.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.authservice.model.OAuthProviders;

@Repository
public interface OAuthProvidersRepo extends JpaRepository<OAuthProviders, Long>{

	OAuthProviders findByProviderAndProviderid(String provider,String providerid);
}
