package com.example.product_service.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.product_service.feign.CartMicroService;
import com.example.product_service.feign.CommentMicroService;
import com.example.product_service.model.Products;
import com.example.product_service.projection.ProductView;
//import com.example.product_service.repository.CartRepo;
//import com.example.product_service.repository.CommentRepo;
import com.example.product_service.repository.ProductRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductService {
	
	private final ProductRepo productrepo;
	private final CartMicroService cartservice;
	private final CommentMicroService commentservice;
	
	public ResponseEntity<String> addProduct(Products product) {
		productrepo.save(product);
		return ResponseEntity.status(HttpStatus.OK).body("Product added successfully");
	}
	
	public List<ProductView> fetchProductsByCategory(String category){
		return productrepo.findByCategory(category);
	}
	
	public Products getProductById(long id) {
		return productrepo.findById(id).orElse(null);
	}
	
	public ResponseEntity<String> updateProduct(Products product){
		productrepo.save(product);
		return ResponseEntity.status(HttpStatus.OK).body("Product updated successfully");
	}
	
	public ResponseEntity<String> deleteProduct(long id){
		cleanUpProducts(id);
		productrepo.deleteById(id);
		return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
	}
	
	public List<ProductView> fetchAllProducts(){
		return productrepo.findAllProducts();
	}
	
	public void updateStock(Products product){
		productrepo.save(product);
	}
	
	private void cleanUpProducts(long productid) {
		try {
			cartservice.deleteCartItemsByProductId(productid);
		}catch(Exception e) {
			System.err.println("Cart cleanup failed for productId: "+ productid);
			e.printStackTrace();
		}
		
		try {
			commentservice.deleteCommentsByProductId(productid);
		}catch(Exception e) {
			System.err.println("Comment cleanup failed for productId: "+ productid);
			e.printStackTrace();
		}		
	}
}
