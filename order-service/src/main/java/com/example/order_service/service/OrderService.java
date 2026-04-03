package com.example.order_service.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.order_service.feign.ProductMicroService;
import com.example.order_service.model.Orders;
import com.example.order_service.model.Products;
import com.example.order_service.model.Users;
import com.example.order_service.repository.OrdersRepo;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;

@Service
public class OrderService {
	
	private final OrdersRepo orderrepo;
	private final MailService mailservice;
	private ProductMicroService productservice;
	
	public OrderService(OrdersRepo orderrepo, MailService mailservice,ProductMicroService productservice) {
		this.orderrepo = orderrepo;
		this.mailservice = mailservice;
		this.productservice = productservice;
	}
	
	@Value("${razorpay.client.id}")
	private String clientid;
	@Value("${razorpay.client.secret}")
	private String clientsecret;
		
	public ResponseEntity<String> placeOrder(Orders order,Products product,Users user) {
		productservice.updateStock(product);
		orderrepo.save(order);
		mailservice.orderConfirmationMail(product, user, order);
		return ResponseEntity.ok("Product purchased successfully");
	}
	
	public List<Orders> fetchOrderByUser(Users user){
		return orderrepo.findByUserid(user.getId());
	}
	
	public List<Orders> fetchOrders(){
		return orderrepo.findAll();
	}
	
	public ResponseEntity<String> cancelOrder(long orderid) throws RazorpayException{
		Orders order = orderrepo.findById(orderid).orElse(null);
		if(order == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order details not found");
		System.out.println("Order info : "+order);
		if("RAZORPAY".equals(order.getPaymentmethod())) {
			RazorpayClient client = new RazorpayClient(clientid, clientsecret);
			JSONObject refundreq = new JSONObject();
			refundreq.put("payment_id", order.getPaymentid());
			Refund refund = client.payments.refund(order.getPaymentid(), refundreq);
			String refundid = refund.get("id");
			String status = refund.get("status");
			order.setPayment_Status("REFUNDED");
			order.setRefundid(refundid);
			order.setRefundstatus(status);
		}
		else
			order.setPayment_Status("CANCELLED");
		order.setOrder_status("CANCELLED");
		orderrepo.save(order);
		return ResponseEntity.ok("Order cancelled successfully");
	}
	
	public Orders buildOrder(long productid, Users user, String paymentMethod, String paymentStatus, 
			String paymentId) {
	    
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

	    if(paymentId != null) {
	        order.setPaymentid(paymentId);
	    }

	    return order;
	}
}
