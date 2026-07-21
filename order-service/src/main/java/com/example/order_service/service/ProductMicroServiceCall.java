package com.example.order_service.service;

import org.springframework.stereotype.Service;

import com.example.order_service.feign.ProductMicroService;
import com.example.order_service.model.Products;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductMicroServiceCall {
	
	private final ProductMicroService productService;
	
	@CircuitBreaker(name = "product-service" , fallbackMethod = "fetchProductByIdFallBack")
	public Products fetchProductById(long id) {
		return productService.fetchProductById(id);
	}
	
	@CircuitBreaker(name = "product-service" , fallbackMethod = "updateStockFallBack")
	public void updateStock(Products product) {
		productService.updateStock(product);
	}
	
	public Products fetchProductByIdFallBack(long id , Throwable t) {
		Products product = new Products();
		System.out.println("Unable To Fetch The Product At This Time");
		return product;
	}
	
	public void updateStockFallBack(Products product , Throwable t) {
		System.out.println("Unable To Update The Product Stock At This Time");
	}
	
}
