package com.example.cart_service.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.cart_service.model.Cart;

@Repository
public interface CartRepo extends MongoRepository<Cart, ObjectId>{
	Page<Cart> findByUserId(long userid , Pageable pageable);
	void deleteByUserId(Long userid);
	void deleteByProductId(Long productid);
	long countByUserId(long userid);
	List<Cart> findByUserId(long userid);
}
