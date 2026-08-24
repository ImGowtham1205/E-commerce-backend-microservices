package com.example.cart_service.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.cart_service.model.Cart;
import com.example.cart_service.repository.CartRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CartCacheService {
	
	private final CartRepo cartRepo;
	
	@Cacheable(value = "cart" , key = "'usercart:' + #userid + ':page:' + #page + 'size:' + #size" 
			, unless = "#result == null")
	public List<Cart>fetchCartProductList(long userid , int page , int size){
		return cartRepo.findByUserId(userid, 
				PageRequest.of(page, size, Sort.by("id").ascending())).getContent();
	}
	
	@Cacheable(value = "cart" , key = "'totalcart:' + #userid" , unless = "#result == 0")
	public long countUserCart(long userid) {
		return cartRepo.countByUserId(userid);
	}
	
}
