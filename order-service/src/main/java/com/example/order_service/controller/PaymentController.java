package com.example.order_service.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.order_service.service.PaymentService;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class PaymentController {
	
	private final PaymentService paymentService;

    @PostMapping("/api/user/create")
    public Map<String, String> createOrder(@RequestBody Map<String,Object> data
    		,HttpServletRequest request) throws RazorpayException  {
    	String token = request.getHeader("Authorization");
    	double amount = Double.parseDouble(data.get("amount").toString());
    	return paymentService.createOrder(amount,token);
    }

    @PostMapping("/api/user/verify")
    public String verifyPayment(@RequestBody Map<String,String> data,HttpServletRequest request) {  
    	String token = request.getHeader("Authorization");
    	data.put("token", token);
        return paymentService.verifyPayment(data);
    }
    
}
