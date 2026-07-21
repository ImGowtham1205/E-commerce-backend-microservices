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

import com.example.order_service.exception.OrderNotFoundException;
import com.example.order_service.model.Orders;
import com.example.order_service.model.Products;
import com.example.order_service.model.UserCache;
import com.example.order_service.service.AuthMicroServiceCall;
import com.example.order_service.service.OrderService;
import com.example.order_service.service.ProductMicroServiceCall;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class OrderController {
	
	private final AuthMicroServiceCall authService;
	private final OrderService orderService;
	private final ProductMicroServiceCall productService;
		
	@PostMapping("/api/user/purchase/{productid}")
	public ResponseEntity<String> purchaseProduct(@PathVariable long productid,
			@RequestBody Map<String,Object> body,HttpServletRequest request){
		
		String paymentMethod = body.get("paymentmethod").toString();
		
		String token = request.getHeader("Authorization");
		UserCache user = authService.userInfo(token);
		
		Products product = productService.fetchProductById(productid);

		String paymentStatus = paymentMethod.equals("COD") ? "NOT_PAID" : "PAID";

		Orders order = orderService.buildOrder(productid, user, paymentMethod, paymentStatus, null);

		product.setStock(product.getStock() - 1);
		orderService.placeOrder(order,product,user);
		return ResponseEntity.ok("Product purchased successfully");		
	}
	
	@GetMapping("/api/user/fetchorder")
	public List<Orders> fetchOrder(HttpServletRequest request){
		String token = request.getHeader("Authorization");
		UserCache user = authService.userInfo(token);
		List<Orders> userorder = orderService.fetchOrderByUser(user);
		return userorder;
	}
	
	@GetMapping("/api/admin/fetchorders")
	public List<Orders> fetchOrders(){
		return orderService.fetchOrders();
	}
	
	@DeleteMapping("/api/user/cancelorder/{orderid}")
	public ResponseEntity<String> cancelOrder(@PathVariable long orderid) 
			throws RazorpayException , OrderNotFoundException{
		orderService.cancelOrder(orderid);
		return ResponseEntity.ok("Order cancelled successfully");
	}
}