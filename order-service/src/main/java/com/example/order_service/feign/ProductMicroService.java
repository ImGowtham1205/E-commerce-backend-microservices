package com.example.order_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.order_service.configuration.FeignConfig;
import com.example.order_service.model.Products;

@FeignClient(name = "product-service",configuration = FeignConfig.class)
public interface ProductMicroService {
	
	@GetMapping("/api/products/details/{id}")
	public Products fetchProductById(@PathVariable long id);
	
	@PutMapping("/api/updatestock")
	public void updateStock(@RequestBody Products product);
}
