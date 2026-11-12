package com.example.authservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.authservice.model.AdminForgotPasswordRequest;
import com.example.authservice.model.AdminRequest;
import com.example.authservice.model.UserForgotPasswordRequest;
import com.example.authservice.model.UserRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class MailService {
	
	private final KafkaTemplate<Long, Object> kafkaTemplate;
		
	public void accountCreationMail(UserRequest user) {
		kafkaTemplate.send("user.created",user.getId(),user).whenComplete((result,ex) -> {
			if(ex != null)
				log.error("Failed To Send user Created Event For Email : "+user.getEmail()+
						" To MailService : " + ex.getMessage());
			else
				log.info("User Created Event is Acutally Delivered For Email : "+user.getEmail()
				+" To MailService Partition : "+result.getRecordMetadata().partition());
		});
	}
	
	public void accountCreationMail(AdminRequest admin) {
		kafkaTemplate.send("admin.created",admin.getId(),admin).whenComplete((result,ex) -> {
			if(ex != null)
				log.error("Failed To Send Admin Created Event For Email : "+admin.getEmail()+
						" To MailService : " + ex.getMessage());
			else
				log.info("Admin Created Event is Acutally Delivered For Email : "+admin.getEmail()
				+" To MailService Partition : "+result.getRecordMetadata().partition());
		});
	}
	
	public void forgotPasswordMail(UserRequest user, String otp) {
		UserForgotPasswordRequest userForgotPasswordRequest = new UserForgotPasswordRequest(user,otp);
	    kafkaTemplate.send("user.forgot-password",user.getId(),userForgotPasswordRequest)
	        .whenComplete((result,ex) -> {
	            if (ex != null) 
	            	log.error("Failed To Send user Forgot Password Event For Email : "
	            			+user.getEmail()+" To MailService : " + ex.getMessage());
	            else 
	            	log.info("User Forgot Password Event is Acutally Delivered For Email : "
	            			+user.getEmail()+" To MailService Partition : "
	            				+result.getRecordMetadata().partition());
	        });
	}
	
	public void forgotPasswordMail(AdminRequest admin,String otp) {
		AdminForgotPasswordRequest adminForgotPasswordRequest = new AdminForgotPasswordRequest(admin,otp);
		kafkaTemplate.send("admin.forgot-password",admin.getId(),adminForgotPasswordRequest)
			.whenComplete((result,ex) -> {
					if (ex != null) 
						System.err.println("Failed To Send Admin Forgot Password Event For Email : "
								+admin.getEmail()+" To MailService : " + ex.getMessage());
		            else 
		            	System.out.println("Admin Forgot Password Event is Acutally Delivered For Email : "
		            			+admin.getEmail()+" To MailService Partition : "
		            				+result.getRecordMetadata().partition());
			});
	}
	
	public void userAccountDeletionMail(UserRequest user) {
		kafkaTemplate.send("user.deleted",user.getId(),user).whenComplete((result,ex) -> {
			if (ex != null) 
				log.error("Failed To Send user Deleted Event For Email : "+user.getEmail()+
						" To MailService : " + ex.getMessage());
            else 
            	log.info("User Deleted Event is Acutally Delivered For Email : "+user.getEmail()
				+" To MailService Partition : "+result.getRecordMetadata().partition());
		});
	}
	
	public void adminAccountDeletionMail(AdminRequest admin) {
		kafkaTemplate.send("admin.deleted",admin.getId(),admin).whenComplete((result,ex) -> {
			if (ex != null) 
				log.error("Failed To Send Admin Created Event For Email : "+admin.getEmail()+
						" To MailService : " + ex.getMessage());
            else 
            	log.info("Admin Created Event is Acutally Delivered For Email : "+admin.getEmail()
				+" To MailService Partition : "+result.getRecordMetadata().partition());
		});
	}
	
}