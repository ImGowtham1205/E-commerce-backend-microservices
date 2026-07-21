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

import com.example.cart_service.exception.CartItemNotFoundException;
import com.example.cart_service.jwt.JwtService;
import com.example.cart_service.model.Cart;
import com.example.cart_service.service.CartService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class CartController {
	
	private final CartService cartService;
	private final JwtService jwtService;
	
	@PostMapping("/api/user/addtocart")
	public ResponseEntity<String> addCart(@RequestBody Cart cart,HttpServletRequest request){
		String token = jwtService.getToken(request);
		long userid = jwtService.extractUserId(token);
		cart.setUserId(userid);
		cartService.addCart(cart);
		return ResponseEntity.ok("Product added to cart successfully");
	}	
	
	@GetMapping("/api/user/getcartitem")
	public List<Cart> fetchCartItemsByUser(HttpServletRequest request){
		String token = jwtService.getToken(request);
		long userid = jwtService.extractUserId(token);
		return cartService.fetchCartProduct(userid);
	}
	
	@DeleteMapping("/api/user/deletecartitem")
	public ResponseEntity<String> deleteSelectedCartItem(@RequestBody String id) 
			throws CartItemNotFoundException{
		id = id.replace("\"", "").trim();
		ObjectId objectid = new ObjectId(id);
		Cart deleteItem = cartService.findByObjectId(objectid);
		cartService.deleteCartItemById(deleteItem);	
		return ResponseEntity.ok("Product removed from your cart successfully");
	}
	
	@PutMapping("/api/user/updatequantity")
	public ResponseEntity<String> updateQuantity(@RequestBody Cart cart){
		cart.setQuantity(cart.getQuantity());
		cartService.updateQuantity(cart);
		return ResponseEntity.ok("Product quantity successfully updated to your cart");
	}
	
	@DeleteMapping("/api/user/deleteusercart/{userid}")
	public void deleteUserCartItems(@PathVariable long userid) {
		cartService.deleteUserCartItems(userid);
	}
	
	@DeleteMapping("/api/admin/deletecartbyproductid/{productid}")
	public void deleteCartItemsByProductId(@PathVariable long productid) {
		cartService.deleteCartItemsByProductId(productid);
	}
}
