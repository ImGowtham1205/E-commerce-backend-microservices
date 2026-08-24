package com.example.product_service.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.product_service.projection.ProductView;
import com.example.product_service.repository.ProductRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductCacheService {
	
	private final ProductRepo productRepo;
	
	 @Cacheable(value = "products",
             key = "'category:' + #category + ':page:' + #page + ':size:' + #size")
  public List<ProductView> fetchProductsByCategoryList(String category, int page, int size) {
      PageRequest pageable = PageRequest.of(page, size, Sort.by("id").ascending());
      return productRepo.findByCategory(category, pageable).getContent();
  }
	
	 @Cacheable(value = "products",
             key = "'all:page:' + #page + ':size:' + #size")
  public List<ProductView> fetchAllProductsList(int page, int size) {
      PageRequest pageable = PageRequest.of(page, size, Sort.by("id").ascending());
      return productRepo.findAllProducts(pageable).getContent();
  }

	 @Cacheable(value = "products",
             key = "'category:total:' + #category")
  public long fetchCategoryCount(String category) {
      return productRepo.countByCategory(category);
  }
	 
	 @Cacheable(value = "products", key = "'all:total'")
	    public long fetchAllCount() {
	        return productRepo.count();
	    }	 
	 
}
