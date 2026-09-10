package com.example.cart_service.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.cart_service.exception.CartItemNotFoundException;
import com.example.cart_service.model.Cart;
import com.example.cart_service.model.Products;
import com.example.cart_service.repository.CartRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CartService {
	
	private final CartRepo cartRepo;
	private final CartCacheService cartcacheService;
	private final ProductService productService;
	
	@CacheEvict(value = "cart" , allEntries = true)
	public Cart addCart(Cart cart){
		return cartRepo.save(cart);
	}
	
	public Page<Cart> fetchCartProduct(long userid , int page , int size){
		List<Cart> content = cartcacheService.fetchCartProductList(userid , page , size);
		long total = cartcacheService.countUserCart(userid);
		return new PageImpl<>(content, PageRequest.of(page, size), total);
	}
	
	@CacheEvict(value = "cart" , allEntries = true)
	public void deleteCartItemById(Cart cart){
		cartRepo.deleteById(cart.getId());
	}
	
	@CacheEvict(value = "cart" , allEntries = true)
	public Cart updateQuantity(Cart cart) {
		return cartRepo.save(cart);
	}
	
	public Cart findByObjectId(ObjectId id) throws CartItemNotFoundException {
		return cartRepo.findById(id)
				.orElseThrow(() -> new CartItemNotFoundException("Cart item not found with id: " + id));
	}
	
	@CacheEvict(value = "cart" , allEntries = true)
	public void deleteUserCartItems(long userid) {
		cartRepo.deleteByUserId(userid);
	}
	
	@CacheEvict(value = "cart", allEntries = true)
	public void deleteCartItemsByProductId(long productid) {
		cartRepo.deleteByProductId(productid);
	}
	
	@Cacheable(value = "cart" , key = "'usercart: ' + #userid + ':totalprice: ' + #result")
	public Double getCartTotal(long userid) {
		return cartRepo.findByUserId(userid)
				.stream()
				.filter(cart -> cart.getQuantity() != 0)
				.mapToDouble(cart -> {
					Products product = productService.fetchProductById(cart.getProductId());
					return product.getPrice() * cart.getQuantity();
				}).sum();
	}
}
