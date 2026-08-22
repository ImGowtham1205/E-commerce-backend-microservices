package com.example.order_service.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.example.order_service.exception.OrderNotFoundException;
import com.example.order_service.feign.ProductMicroService;
import com.example.order_service.model.Orders;
import com.example.order_service.model.Products;
import com.example.order_service.model.UserCache;
import com.example.order_service.repository.OrdersRepo;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class OrderService {

	private final OrdersRepo orderRepo;
	private final MailService mailService;
	private final ProductMicroService productService;
	private final CacheManager cacheManager;
	private final AuthMicroServiceCall authService;
	
	public OrderService(OrdersRepo orderRepo, MailService mailService, 
			ProductMicroService productService ,CacheManager cacheManager , 
				AuthMicroServiceCall authService) {
		this.orderRepo = orderRepo;
		this.mailService = mailService;
		this.productService = productService;
		this.cacheManager = cacheManager;
		this.authService = authService;
	}

	@Value("${razorpay.client.id}")
	private String clientId;
	@Value("${razorpay.client.secret}")
	private String clientSecret;

	@Caching(
			evict = { 
					@CacheEvict(value = "orders", key = "'user :' + #user.id"), 
					@CacheEvict(value = "orders", key = "'all'") }
			)
	public Orders placeOrder(Orders order, Products product, UserCache user) {
		productService.updateStock(product);
		order.setPrice(product.getPrice());
		Orders savedOrder = orderRepo.save(order);
		mailService.orderConfirmationMail(product, user, order);
		return savedOrder;
	}

	@Cacheable(value = "orders", key = "'user :' + #userId")
	public List<Orders> fetchOrderByUser(long userId) {
		return orderRepo.findByUserid(userId);
	}

	@Cacheable(value = "orders", key = "'all'")
	public List<Orders> fetchOrders() {
		return orderRepo.findAll();
	}
	
	@CacheEvict(value = "orders", key = "'all'") 		
	public Orders cancelOrder(long orderid , HttpServletRequest request) 
			throws OrderNotFoundException, RazorpayException {
		try {
			Orders order = orderRepo.findById(orderid)
					.orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderid));
			
			UserCache user = fetchUser(request);
			Products product = productService.fetchProductById(order.getProductid());
			
			if ("RAZORPAY".equals(order.getPaymentmethod())) {
				
				RazorpayClient client = new RazorpayClient(clientId, clientSecret);
				JSONObject refundReq = new JSONObject();
				refundReq.put("amount", Math.round(order.getPrice() * 100));
				Refund refund = client.payments.refund(order.getPaymentid(), refundReq);
				String refundid = refund.get("id");
				String status = refund.get("status");
				order.setPayment_Status("REFUNDED");
				order.setRefundid(refundid);
				order.setRefundstatus(status);
				
			} else
				order.setPayment_Status("CANCELLED");
			
			order.setOrder_status("CANCELLED");
			Orders updatedOrder = orderRepo.save(order);
			cacheManager.getCache("orders").evict("user :" + order.getUserid());
			mailService.orderCancellationMail(product, user, order);
			return updatedOrder;
		}catch (RazorpayException e) {
			e.getMessage();
		    e.printStackTrace();
		    throw e;
		}
		
	}

	public Orders buildOrder(long productid, UserCache user, String paymentMethod, 
			String paymentStatus, String paymentId) {

		Orders order = new Orders();

		order.setUserid(user.getId());
		order.setProductid(productid);
		order.setUsername(user.getName());
		order.setAddress(user.getAddress());
		order.setPhoneno(user.getPhoneno());
		order.setOrderdate(LocalDate.now());
		order.setOrdertime(LocalTime.now());
		order.setPaymentmethod(paymentMethod);
		order.setPayment_Status(paymentStatus);
		
		if (paymentId != null)
			order.setPaymentid(paymentId);

		return order;
	}
	
	public UserCache fetchUser(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		return authService.userInfo(token);
	}
	
}
