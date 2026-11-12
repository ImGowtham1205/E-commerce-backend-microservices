package com.example.cart_service.service;

import org.springframework.stereotype.Service;

import com.example.cart_service.feign.ProductMicroService;
import com.example.cart_service.model.Products;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {
	
	private final ProductMicroService productService;
	
	@CircuitBreaker(name = "product-service" , fallbackMethod = "fetchProductByIdFallBack")
	public Products fetchProductById(long id) {
		log.info("Request sent to product service to fetch product details for productId: {}", id);
		return productService.fetchProductById(id);
	}
	
	public Products fetchProductByIdFallBack(long id , Exception ex) {
		Products product = new Products();
		ex.getMessage();
		log.warn("Unable To Fetch Product Information for ProductId: {} from Product Service. Returning empty product. Exception: {}", id, ex.getMessage());
		return product;
	}
	
}
