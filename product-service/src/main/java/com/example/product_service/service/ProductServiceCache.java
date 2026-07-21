package com.example.product_service.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.example.product_service.model.Products;
import com.example.product_service.repository.ProductRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductServiceCache {
	
	private final ProductRepo productRepo;
	
	@Caching(
		    put = {
		        @CachePut(value = "products", key = "'product:' + #result.id")
		    },
		    evict = {
		        @CacheEvict(value = "products", key = "'products:' + #oldCategory"),
		        @CacheEvict(value = "products", key = "'products:' + #result.category"),
		        @CacheEvict(value = "products", key = "'all'")
		    }
		)
	public Products doUpdateAndEvict(Products product, String oldCategory) {
		    return productRepo.save(product);
		}
	
}
