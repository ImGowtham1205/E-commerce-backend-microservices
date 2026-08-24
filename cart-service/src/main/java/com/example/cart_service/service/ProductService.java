package com.example.cart_service.service;

import org.springframework.stereotype.Service;

import com.example.cart_service.feign.ProductMicroService;
import com.example.cart_service.model.Products;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductService {
	
	private final ProductMicroService productService;
	
	@CircuitBreaker(name = "product-service" , fallbackMethod = "fetchProductByIdFallBack")
	public Products fetchProductById(long id) {
		return productService.fetchProductById(id);
	}
	
	public Products fetchProductByIdFallBack(long id , Exception ex) {
		Products product = new Products();
		ex.getMessage();
		System.err.println("Unable To Fetch Product Information for ProductId : " +id);
		return product;
	}
	
}
