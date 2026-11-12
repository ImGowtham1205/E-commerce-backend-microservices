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

import com.example.comment.exception.CommentNotFoundException;
import com.example.comment.jwt.JwtService;
import com.example.comment.model.Comment;
import com.example.comment.model.UserCache;
import com.example.comment.service.CommentService;
import com.example.comment.service.UserMicroServiceCall;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@Slf4j
public class CommentController {
	
	private final CommentService commentService;
	private final UserMicroServiceCall userService;
	private final JwtService jwtService;
	
	@PostMapping("/api/user/addcomment")
	public ResponseEntity<String> addComment(@RequestBody Comment cmt,HttpServletRequest request){
		String token = request.getHeader("Authorization");
		UserCache user = userService.userInfo(token);
		cmt.setUsername(user.getName());
		
		log.info("Request sent to service layer to add comment for user: {}", user.getId());
		commentService.addComment(cmt);
		
		log.info("Added commment for productid ,userid and updating cache happened successfully for productid : {},{}",cmt.getProductid(),cmt.getUserid());
		return ResponseEntity.ok("Comment Added Successfully");
	}
	
	@PutMapping("/api/user/updatecomment")
	public ResponseEntity<String> updateComment(@RequestBody Comment cmt,HttpServletRequest request){
		String token = request.getHeader("Authorization");
		UserCache user = userService.userInfo(token);
		cmt.setUsername(user.getName());
		cmt.setReview(cmt.getReview());
		
		log.info("Request sent to service layer to update comment for user: {}", user.getId());
		commentService.updateComment(cmt);
		
		log.info("updating commment for productid ,userid and updating cache happened successfully for productid : {},{}",cmt.getProductid(),cmt.getUserid());
		return ResponseEntity.ok("Comment Updated Successfully");
	}
	
	@DeleteMapping("/api/user/deletecomment/{id}")
	public ResponseEntity<String> deleteComment(@PathVariable String id) throws CommentNotFoundException{
		id = id.replace("\"", "").trim();
		ObjectId objectid = new ObjectId(id);
		Comment deleteItem = commentService.fetchCommentById(objectid);
		
		log.info("Request sent to service layer to delete comment for user: {}", deleteItem.getUserid());
		commentService.deleteComment(deleteItem);
		
		log.info("Deleted commment for productid ,userid and updating cache happened successfully for productid : {},{}",deleteItem.getProductid(),deleteItem.getUserid());
		return ResponseEntity.ok("Comment Deleted Successfully");
	}
	
	@GetMapping("/api/user/commentcount/{productid}")
	public ResponseEntity<Long> getCommentCount(@PathVariable long productid){
		long count = commentService.getCommentCountForProduct(productid);
		
		log.info("Fetched comment count happened successfully for productid : {}", productid);
		return ResponseEntity.ok(count);
	}
	
	@GetMapping("/api/user/getcomments/{productid}")
	public List<Comment> getCommentsForproduct(@PathVariable long productid){
		List<Comment> comments = commentService.getCommentForproduct(productid);
		
		log.info("Fetched comments happened successfully for productid : {}", productid);
		return comments;
	}
	
	@GetMapping("/api/user/getuserid")
	public ResponseEntity<Long> getUserId(HttpServletRequest request) {
		String token = jwtService.getToken(request);
		long id = jwtService.extractUserId(token);
		
		log.info("Fetched user id happened successfully for token : {}", token);
		return ResponseEntity.ok(id);
	}
	
}
