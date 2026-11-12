package com.example.order_service.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.order_service.exception.ProductStockUpdationException;
import com.example.order_service.feign.ProductMicroService;
import com.example.order_service.model.Products;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class ProductMicroServiceCall {
	
	private final ProductMicroService productService;
	
	@CircuitBreaker(name = "product-service" , fallbackMethod = "fetchProductByIdFallBack")
	public Products fetchProductById(long id) {
		log.info("Request sent to Product Service to fetch product with ID: {}", id);
		return productService.fetchProductById(id);
	}
	
	@CircuitBreaker(name = "product-service" , fallbackMethod = "updateStockFallBack")
	public void updateStock(Products product) {
		log.info("Request sent to Product Service to update stock for product with ID: {}", product.getId());
		 ResponseEntity<String> response = productService.updateStock(product);
		 
		 if(!response.getStatusCode().is2xxSuccessful()) {
			 log.error("Failed to update stock for product with ID: {}", product.getId());
			 throw new ProductStockUpdationException
			 	("Unable To Update Product Stock For ProductID : " + product.getId());
		 }
	}
	
	public Products fetchProductByIdFallBack(long id , Exception ex) {
		ex.getStackTrace();
		Products product = new Products();
		log.warn("Unable to fetch product with ID: {}. Exception: {}", id, ex.getMessage());
		return product;
	}
	
	public void updateStockFallBack(Products product , Exception ex) {
		ex.getStackTrace();
		log.warn("Unable to update stock for product with ID: {}. Exception: {}", product.getId(), ex.getMessage());
	}
	
}
