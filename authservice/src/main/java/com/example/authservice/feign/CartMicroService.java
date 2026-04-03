package com.example.authservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.authservice.configuration.FeignConfig;

@FeignClient(name="CART-SERVICE",configuration = FeignConfig.class)
public interface CartMicroService {
	
	@DeleteMapping("/api/user/deleteusercart/{userid}")
	public void deleteUserCartItems(@PathVariable long userid,@RequestHeader("Authorization") String token);
	
}
