package com.example.product_service.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlers {

	@ExceptionHandler(IOException.class)
	public ResponseEntity<String> imageUploadException(){
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add product image");
	}
}
