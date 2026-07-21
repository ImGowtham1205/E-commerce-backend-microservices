package com.example.order_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.order_service.model.Orders;

@Repository
public interface OrdersRepo extends JpaRepository<Orders, Long>{
	List<Orders> findByUserid(long userid);
}
