package com.example.cart_service.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.cart_service.model.Cart;

@Repository
public interface CartRepo extends MongoRepository<Cart, ObjectId>{
	List<Cart> findByUserId(long userid);
	void deleteByUserId(Long userid);
	void deleteByProductId(Long productid);
}
