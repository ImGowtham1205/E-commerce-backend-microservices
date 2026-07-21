package com.example.cart_service.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.cart_service.configuration.FeignConfig;

@FeignClient(name="AUTHSERVICE",configuration = FeignConfig.class)
public interface AuthMicroService {
	
	@GetMapping("/auth/api/getallblacklisttokens")
	public List<String> getallBlackListTokens(@RequestHeader("Authorization") String token);
	
}
