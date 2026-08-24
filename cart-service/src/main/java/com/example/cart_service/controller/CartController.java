package com.example.cart_service.controller;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
		return ResponseEntity.ok("Product Added To Cart Successfully");
	}	
	
	@GetMapping("/api/user/getcartitem")
	public Page<Cart> fetchCartItemsByUser(HttpServletRequest request ,
			@RequestParam(defaultValue = "0") int page ,
			@RequestParam(defaultValue = "10") int size){
		String token = jwtService.getToken(request);
		long userid = jwtService.extractUserId(token);
		return cartService.fetchCartProduct(userid , page , size);
	}
	
	@DeleteMapping("/api/user/deletecartitem")
	public ResponseEntity<String> deleteSelectedCartItem(@RequestBody String id) 
			throws CartItemNotFoundException{
		id = id.replace("\"", "").trim();
		ObjectId objectid = new ObjectId(id);
		Cart deleteItem = cartService.findByObjectId(objectid);
		cartService.deleteCartItemById(deleteItem);	
		return ResponseEntity.ok("Product Removed From Your Cart Successfully");
	}
	
	@PutMapping("/api/user/updatequantity")
	public ResponseEntity<String> updateQuantity(@RequestBody Cart cart){
		cart.setQuantity(cart.getQuantity());
		cartService.updateQuantity(cart);
		return ResponseEntity.ok("Product Quantity Successfully Updated To Your Cart");
	}
	
	@GetMapping("/api/user/carttotal")
	public ResponseEntity<Double> getCartTotal(HttpServletRequest request){
		String token = jwtService.getToken(request);
		long userid = jwtService.extractUserId(token);
		return ResponseEntity.ok(cartService.getCartTotal(userid));
	}
	
	@DeleteMapping("/api/user/deleteusercart/{userid}")
	public ResponseEntity<String> deleteUserCartItems(@PathVariable long userid) throws Exception {
		cartService.deleteUserCartItems(userid);
		return ResponseEntity.ok("User Cart Record Deleted Successfully For UserID : " + userid);
	}
	
	@DeleteMapping("/api/admin/deletecartbyproductid/{productid}")
	public ResponseEntity<String> deleteCartItemsByProductId(@PathVariable long productid) throws Exception {
		cartService.deleteCartItemsByProductId(productid);
		return ResponseEntity.ok("Cart Record Deleted Successfully For Product ID : " + productid);
	}
}
