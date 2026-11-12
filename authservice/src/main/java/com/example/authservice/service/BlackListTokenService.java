package com.example.authservice.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.example.authservice.model.BlackListedToken;
import com.example.authservice.repository.BlackListTokenRepo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class BlackListTokenService {
	
	private final BlackListTokenRepo tokenRepo;
	private final JwtService jwtService;
	
	@Cacheable(value = "blacklisttokens", key = "#token")
	public boolean isBlackListed(String token) {
	    return tokenRepo.existsByToken(token);
	}
	
	@Cacheable(value = "blacklisttokens", key = "'all'")
	public List<String> getAllBlackListTokens(){
		log.info("Fetching all blacklisted tokens from database and caching them");
		return tokenRepo.fetchAllTokens();
	}
	
	@Caching(
			evict = {
				@CacheEvict(value = "blacklisttokens", key = "'all'"),
				@CacheEvict(value = "blacklisttokens", key = "#token")
		})
	public void blackListToken(String token) {
		BlackListedToken b = new BlackListedToken();
		b.setToken(token);
		b.setExiprytime(jwtService.extractExpiration(token));
		log.info("Token blacklisted : " + token + "and evicted from cache successfully");
		tokenRepo.save(b);
	}
}
