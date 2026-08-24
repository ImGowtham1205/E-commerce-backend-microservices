package com.example.order_service.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.order_service.model.Orders;
import com.example.order_service.repository.OrdersRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OrderCacheService {
	
	private final OrdersRepo orderRepo;
	
	@Cacheable(value = "orders", key = "'user :' + #userId + ':page:' + #page")
	public List<Orders> fecthOrderByUserIdList(long userId , int page , int size){
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by("orderid").ascending());
		return orderRepo.findByUserid(userId, pageRequest).getContent();
	}
	
	@Cacheable(value = "orders" , key = "'userorder:total:' + #userid")
	public long fetchUserOrderCount(long userid) {
		return orderRepo.countByuserid(userid);
	}
	
	@Cacheable(value = "orders" , key = "'all:page:' + #page + ':size:' + #size")
	public List<Orders> fetchAllOrdersAsList(int page , int size){
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by("orderid").ascending());
		return orderRepo.findAllOrders(pageRequest).getContent();
	}
	
	@Cacheable(value = "orders" , key = "'total:'")
	public long allOrdersCount() {
		return orderRepo.count();
	}
	
}
