package com.example.comment.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.comment.model.Comment;

@Repository
public interface CommentRepo extends MongoRepository<Comment, ObjectId>{
	 long countByProductid(long productid);
	 List<Comment> findByProductid(long prductid);
	 void deleteByUserid(Long userid);
	 void deleteByproductid(Long productid);
}
