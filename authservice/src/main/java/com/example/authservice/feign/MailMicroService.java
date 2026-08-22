package com.example.authservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.authservice.model.AdminForgotPasswordRequest;
import com.example.authservice.model.Admins;
import com.example.authservice.model.UserForgotPasswordRequest;
import com.example.authservice.model.Users;

@FeignClient(name = "MailService")
public interface MailMicroService {
	
	@PostMapping("/user/sendcreationmail")
	public ResponseEntity<String> sendUserCreationMail(@RequestBody Users user);
	
	@PostMapping("/user/sendforgotpasswordmail")
	public ResponseEntity<String> sendForgotPasswordMailForUser(@RequestBody UserForgotPasswordRequest request);
	
	@PostMapping("/user/accountdeletionmail")
	public ResponseEntity<String> userAccountDeletionMail(@RequestBody Users user);
	
	@PostMapping("/admin/accountdeletionmail")
	public ResponseEntity<String> adminAccountDeletionMail(@RequestBody Admins admin);
	
	@PostMapping("/admin/sendcreationmail")
	public ResponseEntity<String> sendAdminCreationMail(@RequestBody Admins admin);
	
	@PostMapping("/admin/sendforgotpasswordmail")
	public ResponseEntity<String> sendForgotPasswordMailForAdmin(@RequestBody AdminForgotPasswordRequest request);
	
}