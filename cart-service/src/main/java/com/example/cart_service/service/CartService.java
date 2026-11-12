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
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class CartService {
	
	private final CartRepo cartRepo;
	private final CartCacheService cartcacheService;
	private final ProductService productService;
	
	@CacheEvict(value = "cart" , allEntries = true)
	public Cart addCart(Cart cart){
		log.info("Adding item to cart and evicting cache for userId: {}", cart.getUserId());
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
		log.info("Deleted cart item with id: {} and evicted cache for userId: {}", cart.getId(), cart.getUserId());
	}
	
	@CacheEvict(value = "cart" , allEntries = true)
	public Cart updateQuantity(Cart cart) {
		log.info("Updated quantity for cart item with id: {} and evicted cache for userId: {}", cart.getId(), cart.getUserId());
		return cartRepo.save(cart);
	}
	
	public Cart findByObjectId(ObjectId id) throws CartItemNotFoundException {
		log.info("Finding cart item with id: {}", id);
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
					log.info("Calculating total price for userId: {} with productId: {} and quantity: {}", userid, cart.getProductId(), cart.getQuantity());
					return product.getPrice() * cart.getQuantity();
				}
				).sum();
	}
}
