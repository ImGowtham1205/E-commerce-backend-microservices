package com.example.order_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.order_service.model.Orders;

@Repository
public interface OrdersRepo extends JpaRepository<Orders, Long>{
	Page<Orders> findByUserid(long userid , Pageable pageable);
	long countByuserid(long userid);
	@Query("SELECT o FROM Orders o")
	Page<Orders> findAllOrders(Pageable pageable);
}
