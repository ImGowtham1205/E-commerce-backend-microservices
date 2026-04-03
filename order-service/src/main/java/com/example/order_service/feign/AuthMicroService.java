package com.example.order_service.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.order_service.configuration.FeignConfig;
import com.example.order_service.model.Users;

@FeignClient(name="AUTHSERVICE",configuration = FeignConfig.class)
public interface AuthMicroService {
	
	@GetMapping("/auth/api/getallblacklisttokens")
	public List<String> getallBlackListTokens(@RequestHeader("Authorization") String token);
	
	@GetMapping("/auth/api/user/userinfo")
	public Users userInfo(@RequestHeader("Authorization") String token);
	
}
