package com.example.product_service.controller;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.product_service.model.Products;
import com.example.product_service.projection.ProductView;
import com.example.product_service.service.ProductService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ProductController {
	
	private final ProductService productService;
	
	@PostMapping("/admin/addproduct")
	public ResponseEntity<String> addProducts(@RequestPart Products product,
			@RequestPart MultipartFile file) throws IOException{
			product.setImagename(file.getOriginalFilename());
			product.setImagetype(file.getContentType());
			product.setImagedata(file.getBytes());
			productService.addProduct(product);
			
			log.info("Product added successfully with name: {}", product.getProductname());
			return ResponseEntity.status(HttpStatus.OK).body("Product Added Successfully");
	}
	
	@PutMapping("/admin/updateproduct")
	public ResponseEntity<String> updateProduct(@RequestPart Products product , 
			@RequestPart(required = false) MultipartFile file) throws IOException{
			if(file != null && !file.isEmpty()) {
				log.info("Updating product with new image: {}", product.getProductname());
				product.setImagename(file.getOriginalFilename());
				product.setImagetype(file.getContentType());
				product.setImagedata(file.getBytes());
			}			
			
			productService.updateProduct(product);
			
			log.info("Product updated successfully with name: {}", product.getProductname());
			return ResponseEntity.status(HttpStatus.OK).body("Product Updated Successfully");
	}
	
	@DeleteMapping("/admin/deleteproduct/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable long id){
		productService.deleteProduct(id);
		log.info("Product deleted successfully with id: {}", id);
		return ResponseEntity.status(HttpStatus.OK).body("Product Deleted Successfully");
	}
	
	@GetMapping("/products/{category}")
	public Page<ProductView> fetchProductByCategory(@PathVariable String category,
			@RequestParam(defaultValue = "0") int page , 
			@RequestParam(defaultValue = "10") int size){
		return productService.fetchProductsByCategory(category , page , size);
	}
	
	@GetMapping("/products/details/{id}")
	public Products fetchProductById(@PathVariable long id) {
		Products product = productService.getProductById(id);

		log.info("Fetched product details for id: {}", id);
		return product;
	}
	
	@GetMapping("/products/image/{id}")
	public ResponseEntity<byte[]> getProductImage(@PathVariable long id){
		Products product = productService.getProductById(id);
		byte[] imagedata = product.getImagedata();
		
		log.info("Fetched product image for id: {}", id);
		return ResponseEntity.ok()
				.contentType(MediaType.valueOf(product.getImagetype()))
				.body(imagedata);
	}
	
	@GetMapping("/products")
	public Page<ProductView> getproducts(
			@RequestParam(defaultValue = "0") int page , 
			@RequestParam(defaultValue = "10") int size){
		return productService.fetchAllProducts(page , size);
	}
	
	@PutMapping("/updatestock")
	public ResponseEntity<String> updateStock(@RequestBody Products product) {
		productService.updateProduct(product);
		
		log.info("Stock updated for product id: {}", product.getId());
		return ResponseEntity.ok("Stock Updated Successfully for Product ID : " + product.getId());
	}
}