package com.example.comment.exception;

public class CommentNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1756367334200968226L;
	
	public CommentNotFoundException(String msg) {
		super(msg);
	}
	
}
