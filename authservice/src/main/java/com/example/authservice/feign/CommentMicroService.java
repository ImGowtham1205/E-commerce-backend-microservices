package com.example.authservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.authservice.configuration.FeignConfig;

@FeignClient(name="COMMENT",configuration = FeignConfig.class)
public interface CommentMicroService {
	
	@DeleteMapping("/api/user/deleteusercomments/{userid}")
	public ResponseEntity<String> deleteUserComments
		(@PathVariable long userid,@RequestHeader("Authorization") String token);
	
}
