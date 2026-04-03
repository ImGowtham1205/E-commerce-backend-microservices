package com.example.order_service.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.order_service.feign.AuthMicroService;
import com.example.order_service.feign.ProductMicroService;
import com.example.order_service.model.Orders;
import com.example.order_service.model.Products;
import com.example.order_service.model.Users;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

@Service
public class PaymentService {

	private final OrderService orderService;
	private final ProductMicroService productService;
	private final AuthMicroService authService;

	public PaymentService(OrderService orderService,ProductMicroService productService,AuthMicroService authService) {
		this.orderService = orderService;
		this.productService = productService;
		this.authService = authService;
	}

	@Value("${razorpay.client.id}")
	private String clientid;
	@Value("${razorpay.client.secret}")
	private String clientsecret;

	public Map<String, String> createOrder(int amonut, long userid,String token) throws RazorpayException {

		Users user = authService.userInfo(token);
		String email = user.getEmail();

		RazorpayClient client = new RazorpayClient(clientid, clientsecret);

		JSONObject obj = new JSONObject();
		obj.put("amount", amonut * 100);
		obj.put("currency", "INR");
		obj.put("receipt", email + System.currentTimeMillis());

		Order order = client.orders.create(obj);

		Map<String, String> response = new HashMap<>();
		response.put("id", order.get("id").toString());
		response.put("amount", order.get("amount").toString());
		return response;
	}

	public String verifyPayment(Map<String, String> data) {
		try {
			long productid = Long.parseLong(data.get("productid"));
	
			String razorpayOrderId = data.get("razorpay_order_id");
			String razorpayPaymentId = data.get("razorpay_payment_id");
			String razorpaySignature = data.get("razorpay_signature");
			String token = data.get("token");
			
			String payload = razorpayOrderId + "|" + razorpayPaymentId;
			boolean isValid = Utils.verifySignature(payload, razorpaySignature, clientsecret);
			
			if(!isValid)
	            return "Payment verification failed";
	        			
			Products product = productService.fetchProductById(productid);
			Users user = authService.userInfo(token);

			Orders order = orderService.buildOrder(productid, user, "RAZORPAY", "PAID",razorpayPaymentId);
			product.setStock(product.getStock() - 1);
			orderService.placeOrder(order, product, user);
			return "Payment successful";
		} catch (RazorpayException e) {
			e.printStackTrace();
			return "Payment failed";
		}

	}
}