package com.example.cart_service.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.example.cart_service.exception.CartItemNotFoundException;
import com.example.cart_service.model.Cart;
import com.example.cart_service.repository.CartRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CartService {
	
	private final CartRepo cartRepo;
	
	@CacheEvict(value = "cart" , key = "'usercart:' +#cart.userId")
	public Cart addCart(Cart cart){
		return cartRepo.save(cart);
	}
	
	@Cacheable(value = "cart" , key = "'usercart:' +#userid" , unless = "#result == null")
	public List<Cart> fetchCartProduct(long userid){
		return cartRepo.findByUserId(userid);
	}
	
	@Caching(
			evict ={
			   @CacheEvict(value = "cart" , key = "'usercart:' +#cart.userId"),
			   @CacheEvict(value = "cart" , key = "'usercart:' +#cart.id")
			   
			}
    )
	public void deleteCartItemById(Cart cart){
		cartRepo.deleteById(cart.getId());
	}
	
	@Caching(evict = {
		    @CacheEvict(value = "cart", key = "'usercart:' + #cart.userId"),
		    @CacheEvict(value = "cart", key = "'usercart:' + #cart.id")
		})

	public Cart updateQuantity(Cart cart) {
		return cartRepo.save(cart);
	}
	
	@Cacheable(value = "cart" , key = "'usercart:' +#id" , unless = "#result == null")
	public Cart findByObjectId(ObjectId id) throws CartItemNotFoundException {
		return cartRepo.findById(id)
				.orElseThrow(() -> new CartItemNotFoundException("Cart item not found with id: " + id));
	}
	
	@CacheEvict(value = "cart", key = "'usercart:' + #userid")
	public void deleteUserCartItems(long userid) {
		cartRepo.deleteByUserId(userid);
	}
	
	@CacheEvict(value = "cart", allEntries = true)
	public void deleteCartItemsByProductId(long productid) {
		cartRepo.deleteByProductId(productid);
	}
}
