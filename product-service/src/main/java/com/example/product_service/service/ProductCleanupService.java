package com.example.product_service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductCleanupService {

	private final KafkaTemplate<Long, Object> kafkaTemplate;
	
	public void deleteCart(long productid) {
		kafkaTemplate.send("product.deleted", productid, productid).whenComplete((result,ex) -> {
			if(ex != null)
				System.err.println("Failed To Send Product Deleted Event For ProductId : "+productid
						+" To CartService : " + ex.getMessage());
			else
				System.out.println("Product Deleted Event is Acutally Delivered For ProductID : "+productid
						+" To CartService Partition : " + result.getRecordMetadata().partition());
		});
	}
	
	public void deleteComment(long productid) {
		kafkaTemplate.send("product.deleted", productid, productid).whenComplete((result,ex) -> {
			if(ex != null)
				System.err.println("Failed To Send Product Deleted Event For ProductId : "+productid
						+" To CommentService : " + ex.getMessage());
			else
				System.out.println("Product Deleted Event is Acutally Delivered For ProductID : "+productid
						+" To CommentService Partition : " + result.getRecordMetadata().partition());
		});
	}

}
