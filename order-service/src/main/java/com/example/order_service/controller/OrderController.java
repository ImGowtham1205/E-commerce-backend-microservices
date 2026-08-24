package com.example.order_service.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.order_service.exception.OrderNotFoundException;
import com.example.order_service.jwt.JwtService;
import com.example.order_service.model.Orders;
import com.example.order_service.model.Products;
import com.example.order_service.model.UserCache;
import com.example.order_service.service.OrderService;
import com.example.order_service.service.ProductMicroServiceCall;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class OrderController {
	
	private final OrderService orderService;
	private final ProductMicroServiceCall productService;
	private final JwtService jwtService;
	
	@PostMapping("/api/user/purchase/{productid}")
	public ResponseEntity<String> purchaseProduct(@PathVariable long productid,
			@RequestBody Map<String,Object> body,HttpServletRequest request){
		
		String paymentMethod = body.get("paymentmethod").toString();
		
		UserCache user = orderService.fetchUser(request);
		
		Products product = productService.fetchProductById(productid);

		String paymentStatus = paymentMethod.equals("COD") ? "NOT_PAID" : "PAID";

		Orders order = orderService.buildOrder(productid, user, paymentMethod, paymentStatus, null);

		product.setStock(product.getStock() - 1);
		orderService.placeOrder(order,product,user);
		return ResponseEntity.ok("Product purchased successfully");		
	}
	
	@GetMapping("/api/user/fetchorder")
	public Page<Orders> fetchOrder(HttpServletRequest request , 
			@RequestParam(defaultValue = "0") int page ,
			@RequestParam(defaultValue = "6") int size){
		String token = jwtService.getToken(request);
		long userId = jwtService.extractUserId(token);
		return orderService.fetchOrderByUser(userId , page , size); 
	}
	
	@GetMapping("/api/admin/fetchorders")
	public Page<Orders> fetchOrders(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size){
		return orderService.fetchOrders(page , size);
	}
	
	@DeleteMapping("/api/user/cancelorder/{orderid}")
	public ResponseEntity<String> cancelOrder(@PathVariable long orderid , HttpServletRequest request) 
			throws RazorpayException , OrderNotFoundException{
		orderService.cancelOrder(orderid , request);
		return ResponseEntity.ok("Order cancelled successfully");
	}
}