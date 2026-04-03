package com.example.comment.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.comment.configuration.FeignConfig;
import com.example.comment.model.Users;

@FeignClient(name="AUTHSERVICE",configuration = FeignConfig.class)
public interface UsersMicroService {
	
	@GetMapping("/auth/api/user/userinfo")
	public Users userInfo(@RequestHeader("Authorization") String token);
	
	@GetMapping("/auth/api/getallblacklisttokens")
	public List<String> getallBlackListTokens(@RequestHeader("Authorization") String token);
	
}
