package com.example.order_service.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.order_service.exception.OrderAlreadyCancelledException;
import com.example.order_service.exception.OrderNotFoundException;
import com.example.order_service.exception.PaymentMisMatchException;
import com.example.order_service.exception.PaymentNotCapturedException;
import com.example.order_service.model.Orders;
import com.example.order_service.model.Products;
import com.example.order_service.model.UserCache;
import com.example.order_service.repository.OrdersRepo;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {

	private final OrdersRepo orderRepo;
	private final MailService mailService;
	private final ProductMicroServiceCall productService;
	private final CacheManager cacheManager;
	private final AuthMicroServiceCall authService;
	private final OrderCacheService orderCacheService;
	
	public OrderService(OrdersRepo orderRepo, MailService mailService, 
			ProductMicroServiceCall productService ,CacheManager cacheManager , 
				AuthMicroServiceCall authService ,OrderCacheService orderCacheService) {
		this.orderRepo = orderRepo;
		this.mailService = mailService;
		this.productService = productService;
		this.cacheManager = cacheManager;
		this.authService = authService;
		this.orderCacheService = orderCacheService;
	}

	@Value("${razorpay.client.id}")
	private String clientId;
	@Value("${razorpay.client.secret}")
	private String clientSecret;
				
	@CacheEvict(value = "orders", allEntries = true)		
	public Orders placeOrder(Orders order, Products product, UserCache user) {
		productService.updateStock(product);
		order.setPrice(product.getPrice());
		Orders savedOrder = orderRepo.save(order);
		
		log.info("Order placed successfully for product with ID: {} by user: {}", product.getId(), user.getEmail());
		mailService.orderConfirmationMail(product, user, order);
		return savedOrder;
	}

	public Page<Orders> fetchOrderByUser(long userId , int page , int size) {
		List<Orders> userOrders = orderCacheService.fecthOrderByUserIdList(userId, page, size);
		long total = orderCacheService.fetchUserOrderCount(userId);
		return new PageImpl<>(userOrders, PageRequest.of(page, size), total);
	}

	public Page<Orders> fetchOrders(int page , int size) {
		List<Orders> allOrders = orderCacheService.fetchAllOrdersAsList(page , size);
		long total = orderCacheService.allOrdersCount();
		return new PageImpl<>(allOrders, PageRequest.of(page, size) , total);
	}
	
	@CacheEvict(value = "orders", allEntries = true) 		
	public Orders cancelOrder(long orderid , HttpServletRequest request) throws RazorpayException {
       
            Orders order = orderRepo.findById(orderid)
                    .orElseThrow(() -> { log.error("Order with ID: {} not found", orderid);
                    return new OrderNotFoundException("Order not found");
                    });
                    
            if("CANCELLED".equals(order.getOrder_status())) {
            	log.error("Order with ID: {} is already cancelled", orderid);
				throw new OrderAlreadyCancelledException("Order is already cancelled");
            }
            	
            UserCache user = fetchUser(request);
            Products product = productService.fetchProductById(order.getProductid());
            
            try {
            	
            if ("RAZORPAY".equals(order.getPaymentmethod())) {
            	log.info("Initiating refund for order with ID: {} through Razorpay", orderid);
            	
                RazorpayClient client = new RazorpayClient(clientId, clientSecret);
                
                Payment payment = client.payments.fetch(order.getPaymentid());
                String paymentStatus = payment.get("status");
                int amountCaptured = payment.get("amount");
                int amountRefunded = payment.get("amount_refunded");
                int refundAmount = Math.round(order.getPrice() * 100);
                
                if(!"captured".equals(paymentStatus)) {
                	log.error("Payment for order with ID: {} is not captured", orderid);
					throw new PaymentNotCapturedException("Payment is not captured");
                }
                
                long refundableAmount = amountCaptured - amountRefunded;
                
                if(refundAmount > refundableAmount) {
                	log.error("Refund amount for order with ID: {} exceeds refundable amount", orderid);
                	throw new PaymentMisMatchException("Refund amount " + refundAmount + " exceeds refundable "
                			+ "amount " + refundableAmount + " for payment " + order.getPaymentid());
                }
                
                JSONObject refundReq = new JSONObject();
                refundReq.put("amount", refundAmount);
                
                log.info("Initiating refund for order with ID: {} and payment ID: {}", orderid, order.getPaymentid());
                Refund refund = client.payments.refund(order.getPaymentid(), refundReq);
                log.info("Refund successful for order with ID: {} and payment ID: {}. Refund ID: {}", orderid, order.getPaymentid(), refund.get("id"));
                
                String refundid = refund.get("id");
                String status = refund.get("status");
                order.setPayment_Status("REFUNDED");
                order.setRefundid(refundid);
                order.setRefundstatus(status);

            }
               	
             else if ("COD".equals(order.getPaymentmethod())) {
            	log.info("Order with ID: {} is not paid through Razorpay. No refund initiated.", orderid);
                order.setPayment_Status("CANCELLED");
             }  

            order.setOrder_status("CANCELLED");
            Orders updatedOrder = orderRepo.save(order);
            cacheManager.getCache("orders").evict("user :" + order.getUserid());
            log.info("Order with ID: {} cancelled successfully for user: {}", orderid, user.getEmail());
            
            mailService.orderCancellationMail(product, user, order);
            return updatedOrder;
        }catch (RazorpayException e) {
        	log.error("Error occurred while processing refund for order with ID: {}. Error: {}", orderid, e.getMessage());
            e.getMessage();
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
