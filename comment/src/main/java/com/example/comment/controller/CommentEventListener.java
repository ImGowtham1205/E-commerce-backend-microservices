package com.example.comment.controller;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.comment.service.CommentService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CommentEventListener {
	
	private final CommentService commentService;
	
	@KafkaListener(topics = "user.deleted", groupId = "comment-service-group")
	public void deleteUserComments(long userid) {
		commentService.deleteUserComments(userid);
		System.out.println("User Comment Deleted Records is Successfully For UserId : " +userid);
	}
	
	@KafkaListener(topics = "product.deleted", groupId = "comment-service-group")
	public void deleteCommentsByProductId(long productid) {
			commentService.deleteproductComments(productid);
			System.out.println("Product Comment Deleted Records is Successfully For All User , "
					+ "Deleted ProductID : " +productid);
	}
	
}
