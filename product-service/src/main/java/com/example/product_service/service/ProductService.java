package com.example.product_service.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    private final ProductCacheService productCacheService;
     
    public Page<ProductView> fetchProductsByCategory(String category, int page, int size) {
        List<ProductView> content = productCacheService.fetchProductsByCategoryList(category, page, size);
        long total = productCacheService.fetchCategoryCount(category);
        return new PageImpl<>(content, PageRequest.of(page, size), total);
    }

    public Page<ProductView> fetchAllProducts(int page, int size) {
        List<ProductView> content = productCacheService.fetchAllProductsList(page, size);
        long total = productCacheService.fetchAllCount();
        return new PageImpl<>(content, PageRequest.of(page, size), total);
    }

    @Cacheable(value = "product-id", key = "'product:' + #id")
    public Products getProductById(long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product Not Found.."));
    }

    @CacheEvict(value = "products", allEntries = true)
    public Products addProduct(Products product) {
        return productRepo.save(product);
    }
    
    @Caching(
    			evict = {
    				@CacheEvict(value = "products", allEntries = true)
    			},
    			put = {
    				@CachePut(value = "product-id", key = "'product:' + #product.id")
    			}
    		)
    public Products updateProduct(Products product) {
        productRepo.findById(product.getId())
                .orElseThrow(() -> new ProductNotFoundException("Product Not Found.."));
        return productRepo.save(product);
    }

    @Caching(
			evict = {
				@CacheEvict(value = "products", allEntries = true),
				@CacheEvict(value = "product-id", key = "'product:' + #id")
			}
		)
    public Products deleteProduct(long id) {
        Products product = productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product Not Found.."));
        productCleanUpService.deleteCart(id);
        productCleanUpService.deleteComment(id);
        productRepo.deleteById(id);
        return product;
    }
}