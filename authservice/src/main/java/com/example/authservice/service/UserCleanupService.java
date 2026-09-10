package com.example.authservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.authservice.exception.CartRecordDeletionException;
import com.example.authservice.exception.CommentRecordDeletionException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserCleanupService {

	private final KafkaTemplate<Long, Object> kafkaTemplate;
	
	public void deleteCart(long userid) {
		 kafkaTemplate.send("user.deleted", userid, userid).whenComplete((result,ex) -> {
			 if(ex != null)
				 throw new CartRecordDeletionException("Failed To Send User Account Deleted Event To "
				 		+ "CartService : " + ex.getMessage());
				else
					System.out.println("User Deleted Created Event Is Actually Delivered To CartService "
							+ "Partition : " + result.getRecordMetadata().partition());
		 });
	}
	
	public void deleteComment(long userid) {
		kafkaTemplate.send("user.deleted", userid, userid).whenComplete((result,ex) -> {
			if(ex != null)
				throw new CommentRecordDeletionException("Failed To Send User Deleted Created Event To "
						+ "CommentService : " + ex.getMessage());
			else
				System.out.println("User Account Created Event Is Actually Delivered To CommentService "
						+ "Partition : " + result.getRecordMetadata().partition());
		});
	}
}
