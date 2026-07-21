package com.example.product_service.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.example.product_service.exception.ProductNotFoundException;
import com.example.product_service.model.Products;
import com.example.product_service.projection.ProductView;
import com.example.product_service.repository.ProductRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductService {
	
	private final ProductRepo productRepo;
	private final ProductCleanupService productCleanUpService;
	private final ProductServiceCache productServiceCache;
	
	@Cacheable(value = "products" , key = "'products:' + #category" , unless = "#result == null")
	public List<ProductView> fetchProductsByCategory(String category){
		return productRepo.findByCategory(category);
	}
	
	@Cacheable(value = "products" , key = "'product:' + #id" , unless = "#result == null")
	public Products getProductById(long id) {
		return productRepo.findById(id)
				.orElseThrow(()-> new ProductNotFoundException("Product Not Found.."));
	}
	
	@Cacheable(value = "products" , key = "'all'")
	public List<ProductView> fetchAllProducts(){
		return productRepo.findAllProducts();
	}
	
	@Caching(
			evict = {
				@CacheEvict(value = "products" , key = "'products:' + #product.category"),
				@CacheEvict(value = "products" , key = "'all'")
				
			}
		)
	public Products addProduct(Products product) {
		return productRepo.save(product);
	}
	
	@Caching(evict = {
		    @CacheEvict(value = "products", key = "'products:' + #result.category"),
		    @CacheEvict(value = "products", key = "'all'"),
		    @CacheEvict(value = "products", key = "'product:' + #id")
		})
	public Products deleteProduct(long id) {
		    Products product = productRepo.findById(id)
		            .orElseThrow(() -> new ProductNotFoundException("Product Not Found.."));
		    productCleanUpService.deleteCart(id);
		    productCleanUpService.deleteComment(id);
		    productRepo.deleteById(id);
		    return product;
		}

	public Products updateProduct(Products product) {
	    String oldCategory = productRepo.findById(product.getId())
	            .map(Products::getCategory)
	            .orElseThrow(() -> new ProductNotFoundException("Product Not Found.."));
	    return productServiceCache.doUpdateAndEvict(product, oldCategory);
	}
	
}
