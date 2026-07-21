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

@RestController
@AllArgsConstructor
public class CommentController {
	
	private final CommentService commentService;
	private final UserMicroServiceCall userService;
	private final JwtService jwtService;
	
	@PostMapping("/api/user/addcomment")
	public ResponseEntity<String> addComment(@RequestBody Comment cmt,HttpServletRequest request){
		String token = request.getHeader("Authorization");
		UserCache user = userService.userInfo(token);
		cmt.setUsername(user.getName());
		commentService.addComment(cmt);
		return ResponseEntity.ok("Comment added successfully");
	}
	
	@PutMapping("/api/user/updatecomment")
	public ResponseEntity<String> updateComment(@RequestBody Comment cmt,HttpServletRequest request){
		String token = request.getHeader("Authorization");
		UserCache user = userService.userInfo(token);
		cmt.setUsername(user.getName());
		cmt.setReview(cmt.getReview());
		commentService.updateComment(cmt);
		return ResponseEntity.ok("Comment updated successfully");
	}
	
	@DeleteMapping("/api/user/deletecomment/{id}")
	public ResponseEntity<String> deleteComment(@PathVariable String id) throws CommentNotFoundException{
		id = id.replace("\"", "").trim();
		ObjectId objectid = new ObjectId(id);
		Comment deleteItem = commentService.fetchCommentById(objectid);
		commentService.deleteComment(deleteItem);
		return ResponseEntity.ok("Comment deleted successfully");
	}
	
	@GetMapping("/api/user/commentcount/{productid}")
	public ResponseEntity<Long> getCommentCount(@PathVariable long productid){
		long count = commentService.getCommentCountForProduct(productid);
		return ResponseEntity.ok(count);
	}
	
	@GetMapping("/api/user/getcomments/{productid}")
	public List<Comment> getCommentsForproduct(@PathVariable long productid){
		return commentService.getCommentForproduct(productid);
	}
	
	@GetMapping("/api/user/getuserid")
	public ResponseEntity<Long> getUserId(HttpServletRequest request) {
		String token = jwtService.getToken(request);
		long id = jwtService.extractUserId(token);
		return ResponseEntity.ok(id);
	}
	
	@DeleteMapping("/api/user/deleteusercomments/{userid}")
	public void deleteUserCartItems(@PathVariable long userid) {
		commentService.deleteUserComments(userid);
	}
	
	@DeleteMapping("/api/admin/deleteproductcomment/{productid}")
	public void deleteCommentsByProductId(@PathVariable long productid) {
		commentService.deleteproductComments(productid);
	}	
}
