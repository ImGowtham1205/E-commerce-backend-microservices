package com.example.order_service.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.order_service.jwt.JwtService;
import com.example.order_service.model.Orders;
import com.example.order_service.model.Products;
import com.example.order_service.model.UserCache;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentService {

	private final OrderService orderService;
	private final ProductMicroServiceCall productService;
	private final JwtService jwtService;
	private final AuthMicroServiceCall authService;
	
	public PaymentService(OrderService orderService,ProductMicroServiceCall productService,
			JwtService jwtService,AuthMicroServiceCall authService) {
		this.orderService = orderService;
		this.productService = productService;
		this.jwtService = jwtService;
		this.authService = authService;
	}

	@Value("${razorpay.client.id}")
	private String clientid;
	@Value("${razorpay.client.secret}")
	private String clientsecret;

	public Map<String, String> createOrder(double amonut,String token) throws RazorpayException {

		String email = jwtService.extractEmail(token);
		
		try {
			log.info("Initiating capture of amount: {} for user: {}", amonut, email);
			RazorpayClient client = new RazorpayClient(clientid, clientsecret);

			JSONObject obj = new JSONObject();
			obj.put("amount", amonut * 100);
			obj.put("currency", "INR");
			obj.put("receipt", email + System.currentTimeMillis());

			Order order = client.orders.create(obj);
			log.info("Successfully created Razorpay order with ID: {} for user: {}", order.get("id"), email);
			
			Map<String, String> response = new HashMap<>();
			response.put("id", order.get("id").toString());
			response.put("amount", order.get("amount").toString());
			log.info("Returning Razorpay order details for user: {}", email);
			return response;
		}catch (RazorpayException e) {
			log.error("Error occurred while creating Razorpay order for user: {}. Error: {}", email, e.getMessage());
			e.getMessage();
			e.printStackTrace();
			throw e;
		}
	}

	public String verifyPayment(Map<String, String> data) {
		try {
			long productid = Long.parseLong(data.get("productid"));
	
			String razorpayOrderId = data.get("razorpay_order_id");
			String razorpayPaymentId = data.get("razorpay_payment_id");
			String razorpaySignature = data.get("razorpay_signature");
			String token = data.get("token");
			String email = data.get("email");
			
			log.info("Verifying payment for Razorpay order ID: {} and payment ID: {} for user: {}", 
					razorpayOrderId, razorpayPaymentId, email);
			
			String payload = razorpayOrderId + "|" + razorpayPaymentId;
			boolean isValid = Utils.verifySignature(payload, razorpaySignature, clientsecret);
			
			if(!isValid) {
				log.warn("Payment verification failed for Razorpay order ID: {} and payment ID: {} for user: {}", 
						razorpayOrderId, razorpayPaymentId, email);
				return "Payment verification failed";
			}
			
			log.info("payment verification successful for Razorpay order ID: {} and payment ID: {} for user: {}", 
					razorpayOrderId, razorpayPaymentId, email);
			
			Products product = productService.fetchProductById(productid);
			UserCache user = authService.userInfo(token);
			
			log.info("Request sent to service layer to create order object for product with ID: {} by user: {}", 
					productid, email);
			Orders order = orderService.buildOrder(productid, user, "RAZORPAY", "PAID", razorpayPaymentId);
			
			product.setStock(product.getStock() - 1);
			orderService.placeOrder(order, product, user);
			return "Payment successful";
			
		} catch (RazorpayException e) {
			log.error("Error occurred while verifying payment. Error: {}", e.getMessage());
			e.getMessage();
			e.printStackTrace();
			return "Payment failed";
		}

	}
}