package com.example.order_service.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.order_service.jwt.JwtService;
import com.example.order_service.service.PaymentService;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@Slf4j
public class PaymentController {
	
	private final PaymentService paymentService;
	private final JwtService jwtService;
	
    @PostMapping("/api/user/create")
    public Map<String, String> createOrder(@RequestBody Map<String,Object> data
    		,HttpServletRequest request) throws RazorpayException  {
    	String token = jwtService.getToken(request);
    	double amount = Double.parseDouble(data.get("amount").toString());
    	log.info("Request sent to service layer for to capture amount for razorpay order with amount: " + amount + 
    			" for user: " + jwtService.extractEmail(token));
    	return paymentService.createOrder(amount,token);
    }

    @PostMapping("/api/user/verify")
    public String verifyPayment(@RequestBody Map<String,String> data, HttpServletRequest request) { 
    	String authToken = request.getHeader("Authorization");
    	String token = jwtService.getToken(request);
    	String email = jwtService.extractEmail(token);
    	
    	data.put("token", authToken);
    	data.put("email", email);
    	
    	log.info("Request sent to service layer for to Verify amount for razorpay order with payment ID: " 
    			+ data.get("razorpay_payment_id") + " for user: " + email);
    	
        return paymentService.verifyPayment(data);
    }
    
}
