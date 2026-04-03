package com.example.cart_service.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.cart_service.model.Cart;
import com.example.cart_service.repository.CartRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CartService {
	
	private final CartRepo cartrepo;
	
	public ResponseEntity<String> addCart(Cart cart){
		cartrepo.save(cart);
		return ResponseEntity.ok("Product added to cart successfully");
	}
	
	public List<Cart> fetchCartProduct(long userid){
		return cartrepo.findByUserId(userid);
	}
	
	public void deleteCartItemById(ObjectId id){
		cartrepo.deleteById(id);
	}
	
	public void updateQuantity(Cart cart) {
		cartrepo.save(cart);
	}
	
	public void deleteUserCartItems(long userid) {
		cartrepo.deleteByUserId(userid);
	}

	public void deleteCartItemsByProductId(long productid) {
		cartrepo.deleteByProductId(productid);
	}
}
