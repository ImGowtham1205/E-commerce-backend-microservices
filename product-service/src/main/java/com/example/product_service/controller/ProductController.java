package com.example.product_service.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.product_service.model.Products;
import com.example.product_service.projection.ProductView;
import com.example.product_service.service.ProductService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ProductController {
	
	private final ProductService productservice;
	
	@PostMapping("/admin/addproduct")
	public ResponseEntity<String> addProducts(@RequestPart Products product,
			@RequestPart MultipartFile file) throws IOException{
		
		ResponseEntity<String> status = null;
			product.setImagename(file.getOriginalFilename());
			product.setImagetype(file.getContentType());
			product.setImagedata(file.getBytes());
			status = productservice.addProduct(product);
		
		return ResponseEntity.status(status.getStatusCode()).body(status.getBody());
	}
	
	@PutMapping("/admin/updateproduct")
	public ResponseEntity<String> updateProduct(@RequestPart Products product , 
			@RequestPart(required = false) MultipartFile file) throws IOException{
		ResponseEntity<String> status = null;
		
			if(file != null && !file.isEmpty()) {
				product.setImagename(file.getOriginalFilename());
				product.setImagetype(file.getContentType());
				product.setImagedata(file.getBytes());
			}			
			status = productservice.updateProduct(product);
			
		return ResponseEntity.ok(status.getBody());
	}
	
	@DeleteMapping("/admin/deleteproduct/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable long id){
		ResponseEntity<String> status = productservice.deleteProduct(id);
		return ResponseEntity.ok(status.getBody());
	}
	
	@GetMapping("/products/{category}")
	public List<ProductView> fetchProductByCategory(@PathVariable String category){
		return productservice.fetchProductsByCategory(category);
	}
	
	@GetMapping("/products/details/{id}")
	public Products fetchProductById(@PathVariable long id) {
		return productservice.getProductById(id);
	}
	
	@GetMapping("/products/image/{id}")
	public ResponseEntity<byte[]> getProductImage(@PathVariable long id){
		Products product = productservice.getProductById(id);
		byte[] imagedata = product.getImagedata();
		return ResponseEntity.ok()
				.contentType(MediaType.valueOf(product.getImagetype()))
				.body(imagedata);
	}
	
	@GetMapping("/products")
	public List<ProductView> getproducts(){
		return productservice.fetchAllProducts();
	}
	
	@PutMapping("/updatestock")
	public void updateStock(@RequestBody Products product) {
		productservice.updateStock(product);
	}
}