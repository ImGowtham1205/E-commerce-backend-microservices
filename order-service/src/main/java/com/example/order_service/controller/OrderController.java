package com.example.order_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.order_service.feign.AuthMicroService;
import com.example.order_service.feign.ProductMicroService;
import com.example.order_service.model.Orders;
import com.example.order_service.model.Products;
import com.example.order_service.model.Users;
import com.example.order_service.service.OrderService;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class OrderController {
	
	private final AuthMicroService authservice;
	private final OrderService orderservice;
	private final ProductMicroService productservice;
		
	@PostMapping("/api/user/purchase/{productid}")
	public ResponseEntity<String> purchaseProduct(@PathVariable long productid,
			@RequestBody Map<String,Object> body,HttpServletRequest request){
		
		String paymentmethod = body.get("paymentmethod").toString();
		
		String token = request.getHeader("Authorization");
		Users user = authservice.userInfo(token);
		
		Products product = productservice.fetchProductById(productid);

		String paymentStatus = paymentmethod.equals("COD") ? "NOT_PAID" : "PAID";

		Orders order = orderservice.buildOrder(productid, user, paymentmethod, paymentStatus, null);

		product.setStock(product.getStock() - 1);
		ResponseEntity<String> status = orderservice.placeOrder(order,product,user);
		return ResponseEntity.status(status.getStatusCode()).body(status.getBody());		
	}
	
	@GetMapping("/api/user/fetchorder")
	public List<Orders> fetchOrder(HttpServletRequest request){
		String token = request.getHeader("Authorization");
		Users user = authservice.userInfo(token);
		List<Orders> userorder = orderservice.fetchOrderByUser(user);
		return userorder;
	}
	
	@GetMapping("/api/admin/fetchorders")
	public List<Orders> fetchOrders(){
		return orderservice.fetchOrders();
	}
	
	@DeleteMapping("/api/user/cancelorder/{orderid}")
	public ResponseEntity<String> cancelOrder(@PathVariable long orderid) throws RazorpayException{
		ResponseEntity<String> status = orderservice.cancelOrder(orderid);
		return ResponseEntity.status(status.getStatusCode()).body(status.getBody());
	}
}