package com.example.product_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.product_service.configuration.FeignConfig;

@FeignClient(name = "COMMENT" , configuration = FeignConfig.class)
public interface CommentMicroService {
	
	@DeleteMapping("/api/admin/deleteproductcomment/{productid}")
	public void deleteCommentsByProductId(@PathVariable long productid);
	
}
