package com.example.product_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.product_service.configuration.FeignConfig;

@FeignClient(name = "CART-SERVICE",configuration = FeignConfig.class)
public interface CartMicroService {
	
	@DeleteMapping("/api/admin/deletecartbyproductid/{productid}")
	public void deleteCartItemsByProductId(@PathVariable long productid);
	
}
