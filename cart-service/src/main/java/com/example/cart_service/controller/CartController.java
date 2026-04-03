package com.example.cart_service.controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.cart_service.jwt.JwtService;
import com.example.cart_service.model.Cart;
import com.example.cart_service.service.CartService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class CartController {
	
	private final CartService cartservice;
	private final JwtService jwtservice;
	
	@PostMapping("/api/user/addtocart")
	public ResponseEntity<String> addCart(@RequestBody Cart cart,HttpServletRequest request){
		String token = jwtservice.getToken(request);
		long userid = jwtservice.extractUserId(token);
		cart.setUserId(userid);
		ResponseEntity<String> status = cartservice.addCart(cart);
		return ResponseEntity.ok(status.getBody());
	}	
	
	@GetMapping("/api/user/getcartitem")
	public List<Cart> fetchCartItemsByUser(HttpServletRequest request){
		String token = jwtservice.getToken(request);
		long userid = jwtservice.extractUserId(token);
		return cartservice.fetchCartProduct(userid);
	}
	
	@DeleteMapping("/api/user/deletecartitem")
	public ResponseEntity<String> deleteSelectedCartItem(@RequestBody String id){
		id = id.replace("\"", "").trim();
		ObjectId objectid = new ObjectId(id);
		cartservice.deleteCartItemById(objectid);
		return ResponseEntity.ok("Product removed from your cart successfully");
	}
	
	@PutMapping("/api/user/updatequantity")
	public ResponseEntity<String> updateQuantity(@RequestBody Cart cart){
		cart.setQuantity(cart.getQuantity());
		cartservice.updateQuantity(cart);
		return ResponseEntity.ok("Product quantity successfully updated to your cart");
	}
	
	@DeleteMapping("/api/user/deleteusercart/{userid}")
	public void deleteUserCartItems(@PathVariable long userid) {
		cartservice.deleteUserCartItems(userid);
	}
	
	@DeleteMapping("/api/admin/deletecartbyproductid/{productid}")
	public void deleteCartItemsByProductId(@PathVariable long productid) {
		cartservice.deleteCartItemsByProductId(productid);
	}
}
