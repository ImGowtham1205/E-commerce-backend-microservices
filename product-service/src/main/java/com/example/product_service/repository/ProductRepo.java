package com.example.product_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.product_service.model.Products;
import com.example.product_service.projection.ProductView;

@Repository
public interface ProductRepo extends JpaRepository<Products, Long>{
	Page<ProductView> findByCategory(String category , Pageable page);
	@Query("""
	        SELECT p.id AS id,
	               p.productname AS productname,
	               p.description AS description,
	               p.price AS price,
	               p.stock AS stock,
	               p.category AS category
	        FROM Products p
	    """)
	Page<ProductView> findAllProducts(Pageable page);
	long countByCategory(String category);
}
