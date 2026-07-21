package com.example.comment.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.example.comment.exception.CommentNotFoundException;
import com.example.comment.model.Comment;
import com.example.comment.repository.CommentRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentService {
	
	private final CommentRepo commentRepo;
	
	@Cacheable(value = "comment" , key = "'cmtcount:' + #productid" , unless = "#result == null")
	public long getCommentCountForProduct(long productid) {
		return commentRepo.countByProductid(productid);
	}
	
	@Cacheable(value = "comment" , key = "'productcmt:' + #productid" , unless = "#result == null")
	public List<Comment> getCommentForproduct(long productid){
		return commentRepo.findByProductid(productid);
	}
	
	@Caching(
			evict = {
				@CacheEvict(value = "comment" , key = "'productcmt:' + #cmt.productid"),
				@CacheEvict(value = "comment" , key = "'cmtcount:' + #cmt.productid")
			})
	public Comment addComment(Comment cmt){
		return commentRepo.save(cmt);
	}
	
	@Caching(
			evict = {
				@CacheEvict(value = "comment" , key = "'productcmt:' + #cmt.productid"),
				@CacheEvict(value = "comment" , key = "'cmtcount:' + #cmt.productid")
			})
	public Comment updateComment(Comment cmt){
		return commentRepo.save(cmt);
	}
	
	@Caching(
				evict = {
					@CacheEvict(value = "comment" , key = "'productcmt:' + #cmt.productid"),
					@CacheEvict(value = "comment" , key = "'cmtcount:' + #cmt.productid")
				})
	public void deleteComment(Comment cmt){
		commentRepo.deleteById(cmt.getId());
	}
	
	@Caching(
			evict = {
				@CacheEvict(value = "comment" , key = "'productcmt:' + #productid"),
				@CacheEvict(value = "comment" , key = "'cmtcount:' + #productid")
			})
	public void deleteproductComments(long productid) {
		commentRepo.deleteByproductid(productid);
	}
	
	public void deleteUserComments(long userid) {
		commentRepo.deleteByUserid(userid);
	}
	
	public Comment fetchCommentById(ObjectId id) {
		return commentRepo.findById(id)
				.orElseThrow(()-> new CommentNotFoundException("Comment Not Found.Unable To Delete"));
	}
	
}
