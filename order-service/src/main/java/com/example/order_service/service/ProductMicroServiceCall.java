package com.example.order_service.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.order_service.exception.ProductStockUpdationException;
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
		 ResponseEntity<String> response = productService.updateStock(product);
		 
		 if(!response.getStatusCode().is2xxSuccessful())
			 throw new ProductStockUpdationException
			 	("Unable To Update Product Stock For ProductID : " + product.getId());
		 
	}
	
	public Products fetchProductByIdFallBack(long id , Exception ex) {
		ex.getStackTrace();
		Products product = new Products();
		System.err.println("Unable To Fetch Product Information For ProductID : " + id);
		return product;
	}
	
	public void updateStockFallBack(Products product , Exception ex) {
		ex.getStackTrace();
		System.err.println("Unable To Update Product Stock For ProductID : " + product.getId());
	}
	
}
