package com.example.MailService.service;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.MailService.model.Admins;
import com.example.MailService.model.Orders;
import com.example.MailService.model.Products;
import com.example.MailService.model.UserCache;
import com.example.MailService.model.Users;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MailService {
	
	private final JavaMailSender sender;
	
	public void accountCreationMail(Users user) throws MailException , Exception {
		
		String subject = "Welcome to AzCart – Your Account Has Been Created Successfully 🎉";
		String receiver = user.getEmail();
		String body = "Dear "+user.getName()+",\r\n"
				+ "\r\n"
				+ "Welcome to AzCart!\r\n"
				+ "\r\n"
				+ "We’re excited to inform you that your AzCart account has been created successfully using this email address.\r\n"
				+ "\r\n"
				+ "You can now log in and start exploring our platform to discover products, offers, and a seamless shopping experience.\r\n"
				+ "\r\n"
				+ "If you did not create this account or believe this was a mistake, please contact our support team immediately.\r\n"
				+ "\r\n"
				+ "For security reasons, never share your login credentials with anyone.\r\n"
				+ "\r\n"
				+ "Thank you for choosing AzCart.\r\n"
				+ "We’re glad to have you with us!\r\n"
				+ "\r\n"
				+ "Best regards,  \r\n"
				+ "AzCart Support Team  \r\n"
				+ "Email: azcart.noreply@gmail.com\r\n"
				+ "";
		
		send(receiver, subject, body);
	}
	
	public void forgotPasswordMail(Users user,String token) throws MailException , Exception {
		
		String subject = "AZCART – Reset Your Password";
		String receiver =  user.getEmail();
		String url = "http://localhost:5173/reset-password?token="+token;
		String body = "Hello "+user.getName()+",\r\n"
				+ "\r\n"
				+ "We received a request to reset your AZCART account password.\r\n"
				+ "\r\n"
				+ "To create a new password, please click the link below:\r\n"
				+ url+"\r\n"
				+ "\r\n"
				+ "For your security, this link will expire in 15 minutes and can be used only once.\r\n"
				+ "\r\n"
				+ "If you did not request a password reset, please ignore this email. Your account will remain secure.\r\n"
				+ "\r\n"
				+ "Password requirements:\r\n"
				+ "• Minimum 8 characters\r\n"
				+ "• At least 1 uppercase letter\r\n"
				+ "• At least 1 lowercase letter\r\n"
				+ "• At least 1 number\r\n"
				+ "• At least 1 special character\r\n"
				+ "\r\n"
				+ "Thank you for choosing AZCART.\r\n"
				+ "\r\n"
				+ "Best regards,\r\n"
				+ "AZCART Support Team\r\n"
				+ "azcart.noreply@gmail.com\r\n"
				+ "";
		
		send(receiver, subject, body);
	}
	
	public void forgotPasswordMail(Admins admin,String token) throws MailException , Exception {
		
		String subject = "AZCART – Reset Your Password";
		String receiver =  admin.getEmail();
		String url = "http://localhost:5173/reset-password?token="+token;
		String body = "Hello "+admin.getAdminName()+",\r\n"
				+ "\r\n"
				+ "We received a request to reset your AZCART account password.\r\n"
				+ "\r\n"
				+ "To create a new password, please click the link below:\r\n"
				+ url+"\r\n"
				+ "\r\n"
				+ "For your security, this link will expire in 15 minutes and can be used only once.\r\n"
				+ "\r\n"
				+ "If you did not request a password reset, please ignore this email. Your account will remain secure.\r\n"
				+ "\r\n"
				+ "Password requirements:\r\n"
				+ "• Minimum 8 characters\r\n"
				+ "• At least 1 uppercase letter\r\n"
				+ "• At least 1 lowercase letter\r\n"
				+ "• At least 1 number\r\n"
				+ "• At least 1 special character\r\n"
				+ "\r\n"
				+ "Thank you for choosing AZCART.\r\n"
				+ "\r\n"
				+ "Best regards,\r\n"
				+ "AZCART Support Team\r\n"
				+ "azcart.noreply@gmail.com\r\n"
				+ "";
		
		send(receiver, subject, body);
	}

	public void userAccountDeletionMail(Users user) throws MailException , Exception {
			
		String subject = "Confirmation of Account Deletion";
		String reciever = user.getEmail();
		String body = "Dear Valued Customer "+user.getName()+",\r\n"
				+ "\r\n"
				+ "We are writing to confirm that your account associated with this email address has been successfully deleted from our system, as per your request.\r\n"
				+ "\r\n"
				+ "Please note that this action is permanent. All personal information and account-related data linked to your profile have been removed in accordance with our data retention and privacy policies.\r\n"
				+ "Kindly be informed that order history alone is securely retained for internal management, auditing, and legal compliance purposes, as required by applicable regulations.\r\n"
				+ "\r\n"
				+ "If you did not initiate this request or believe this action was taken in error, please contact our support team immediately for assistance.\r\n"
				+ "\r\n"
				+ "Thank you for using our services. We appreciate the time you spent with us and hope to serve you again in the future.\r\n"
				+ "\r\n"
				+ "Warm regards,\r\n"
				+ "AzCart Support Team\r\n"
				+ "Customer Support\r\n"
				+ "AzCart";
	
		send(reciever, subject, body);
	}
	
	public void adminAccountDeletionMail(Admins admin) throws MailException , Exception {
	
		String subject = "Admin Account Deletion Notification";
		String receiver = admin.getEmail();
		String body = "Dear Administrator "+admin.getAdminName()+",\r\n"
				+ "\r\n"
				+ "This email is to inform you that a user account has been successfully deleted from the system following a valid deletion request.\r\n"
				+ "\r\n"
				+ "The account associated with the registered email address has been permanently removed. All personal and account-related information has been deleted in accordance with the organization’s data retention and privacy policies.\r\n"
				+ "Please note that order history has been securely retained for internal management, auditing, and compliance purposes only.\r\n"
				+ "\r\n"
				+ "No further action is required at this time. This notification is provided for administrative awareness and record-keeping.\r\n"
				+ "\r\n"
				+ "If additional verification or review is necessary, please refer to the system logs or contact the support team.\r\n"
				+ "\r\n"
				+ "Regards,\r\n"
				+ "AzCart System Notification\r\n"
				+ "Administration & Support\r\n"
				+ "AzCart";
		
		send(receiver, subject, body);
	}
	
	public void orderConfirmationMail(Products product, UserCache user, Orders order) 
				throws MailException , Exception {
		
		String subject = "Order Confirmation – Your Order Has Been Successfully Placed";
		String receiver = user.getEmail();
		String body = "Dear "+user.getName()+",\r\n"
				+ "\r\n"
				+ "Thank you for shopping with AzCart. We are pleased to confirm that your order has been successfully placed and is currently being processed.\r\n"
				+ "\r\n"
				+ "Order Details:\r\n"
				+ "Order ID: "+order.getOrderid()+"\r\n"
				+ "Order Date: "+order.getOrderdate()+"\r\n"
				+ "Total Amount: ₹"+product.getPrice()+"\r\n"
				+ "\r\n"
				+ "Delivery Address:\r\n"
				+ user.getAddress()+"\r\n"
				+ "\r\n"
				+ "Item Ordered:\r\n"
				+ product.getProductname()+"\r\n"
				+ "\r\n"
				+ "You will receive further updates once your order has been shipped.\r\n"
				+ "If you have any questions or require assistance, please contact our support team at support@azcart.noreply@gmail.com.\r\n"
				+ "\r\n"
				+ "Thank you for choosing AzCart.\r\n"
				+ "\r\n"
				+ "Sincerely,\r\n"
				+ "AzCart Team\r\n";
		
		send(receiver, subject, body);
	}
	
	public void adminAccountCreationMail(Admins admin) throws MailException , Exception {
		
		String subject = "Welcome to AzCart Admin Panel – Account Created Successfully 🎉";
		String receiver = admin.getEmail();
		String body = "Dear "+admin.getAdminName()+",\r\n"
				+ "\r\n"
				+ "Welcome to the AzCart Admin Panel!\r\n"
				+ "\r\n"
				+ "We’re pleased to inform you that your administrator account has been created successfully using this email address.\r\n"
				+ "\r\n"
				+ "You can now log in to the admin dashboard to manage products, orders, and users on the AzCart platform.\r\n"
				+ "\r\n"
				+ "If you did not request this account or believe this was a mistake, please contact the system administrator immediately.\r\n"
				+ "\r\n"
				+ "For security reasons, never share your admin login credentials with anyone.\r\n"
				+ "\r\n"
				+ "Thank you for being a part of the AzCart team.\r\n"
				+ "\r\n"
				+ "Best regards,\r\n"
				+ "AzCart Support Team\r\n"
				+ "Email: azcart.noreply@gmail.com\r\n"
				+ "";

		send(receiver, subject, body);
	}

	public void orderCancellationMail(Products product, UserCache user, Orders order) 
			throws MailException , Exception {
		
		String subject = "Order Cancelled – Your AzCart Order Has Been Cancelled";
		String receiver = user.getEmail();
		String body = "Dear "+user.getName()+",\r\n"
				+ "\r\n"
				+ "We would like to inform you that your order has been successfully cancelled as per your request.\r\n"
				+ "\r\n"
				+ "Order Details:\r\n"
				+ "Order ID: "+order.getOrderid()+"\r\n"
				+ "Order Date: "+order.getOrderdate()+"\r\n"
				+ "Cancelled Item: "+product.getProductname()+"\r\n"
				+ "Amount: ₹"+product.getPrice()+"\r\n"
				+ "\r\n"
				+ "If a payment was made for this order, the refund (if applicable) will be processed to your original payment method within 5–7 business days.\r\n"
				+ "\r\n"
				+ "If you did not request this cancellation or believe this was a mistake, please contact our support team immediately at supportazcart.noreply@gmail.com.\r\n"
				+ "\r\n"
				+ "We hope to serve you again soon.\r\n"
				+ "\r\n"
				+ "Sincerely,\r\n"
				+ "AzCart Team\r\n";
		
		send(receiver, subject, body);
	}
	
	private void send(String receiver, String subject, String body) throws MailException , Exception {
	    SimpleMailMessage message = new SimpleMailMessage();
	    message.setTo(receiver);
	    message.setSubject(subject);
	    message.setText(body);
	    sender.send(message);
	}
	
}
