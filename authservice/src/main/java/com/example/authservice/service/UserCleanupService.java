package com.example.authservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.authservice.exception.CartRecordDeletionException;
import com.example.authservice.exception.CommentRecordDeletionException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserCleanupService {

	private final KafkaTemplate<Long, Object> kafkaTemplate;
	
	public void deleteCart(long userid) {
		 kafkaTemplate.send("user.deleted", userid, userid).whenComplete((result,ex) -> {
			 if(ex != null) {
				 log.error("Failed To Send User Deleted Created Event To CartService : " + ex.getMessage());
				 throw new CartRecordDeletionException("Failed To Send User Account Deleted Event To "
					 		+ "CartService : " + ex.getMessage());
			 }
				else
					log.info("User Account Deleted Event Is Actually Delivered To CartService "
							+ "Partition : " + result.getRecordMetadata().partition());
		 });
	}
	
	public void deleteComment(long userid) {
		kafkaTemplate.send("user.deleted", userid, userid).whenComplete((result,ex) -> {
			if(ex != null) {
				log.error("Failed To Send User Deleted Created Event To CommentService : " + ex.getMessage());
				throw new CommentRecordDeletionException("Failed To Send User Deleted Created Event To "
						+ "CommentService : " + ex.getMessage());
			}
			else
				log.info("User Account Deleted Event Is Actually Delivered To CommentService "
						+ "Partition : " + result.getRecordMetadata().partition());
		});
	}
}
