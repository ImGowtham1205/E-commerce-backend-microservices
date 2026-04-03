package com.example.comment.controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.comment.feign.UsersMicroService;
import com.example.comment.model.Comment;
import com.example.comment.model.Users;
import com.example.comment.service.CommentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class CommentController {
	
	private final CommentService commentservice;
	private final UsersMicroService userservice;
	
	@PostMapping("/api/user/addcomment")
	public ResponseEntity<String> addComment(@RequestBody Comment cmt,HttpServletRequest request){
		String token = request.getHeader("Authorization");
		Users user = userservice.userInfo(token);
		cmt.setUsername(user.getName());
		ResponseEntity<String> status = commentservice.addComment(cmt);
		return ResponseEntity.status(status.getStatusCode()).body(status.getBody());
	}
	
	@PutMapping("/api/user/updatecomment")
	public ResponseEntity<String> updateComment(@RequestBody Comment cmt,HttpServletRequest request){
		String token = request.getHeader("Authorization");
		Users user = userservice.userInfo(token);
		cmt.setUsername(user.getName());
		cmt.setReview(cmt.getReview());
		ResponseEntity<String> status = commentservice.updateComment(cmt);
		return ResponseEntity.status(status.getStatusCode()).body(status.getBody());
	}
	
	@DeleteMapping("/api/user/deletecomment/{id}")
	public ResponseEntity<String> deleteComment(@PathVariable String id){
		id = id.replace("\"", "").trim();
		ObjectId objectid = new ObjectId(id);
		ResponseEntity<String> status = commentservice.deleteComment(objectid);
		return ResponseEntity.status(status.getStatusCode()).body(status.getBody());
	}
	
	@GetMapping("/api/user/commentcount/{productid}")
	public ResponseEntity<Long> getCommentCount(@PathVariable long productid){
		long count = commentservice.getCommentCountForProduct(productid);
		return ResponseEntity.ok(count);
	}
	
	@GetMapping("/api/user/getcomments/{productid}")
	public List<Comment> getCommentsForproduct(@PathVariable long productid){
		return commentservice.getCommentForproduct(productid);
	}
	
	@GetMapping("/api/user/getuserid")
	public ResponseEntity<Long> getUserId(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		Users user = userservice.userInfo(token);
		long id = user.getId();
		return ResponseEntity.ok(id);
	}
	
	@DeleteMapping("/api/user/deleteusercomments/{userid}")
	public void deleteUserCartItems(@PathVariable long userid) {
		commentservice.deleteUserComments(userid);
	}
	
	@DeleteMapping("/api/admin/deleteproductcomment/{productid}")
	public void deleteCommentsByProductId(@PathVariable long productid) {
		commentservice.deleteproductComments(productid);
	}	
}
