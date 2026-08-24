package com.example.cart_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.cart_service.configuration.FeignConfig;
import com.example.cart_service.model.Products;

@FeignClient(name = "product-service" , configuration = FeignConfig.class)
public interface ProductMicroService {
	
	@GetMapping("/api/products/details/{id}")
	public Products fetchProductById(@PathVariable long id);
	
}
