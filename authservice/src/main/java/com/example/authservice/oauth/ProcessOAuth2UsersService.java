package com.example.authservice.oauth;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.example.authservice.model.AdminOAuthProviders;
import com.example.authservice.model.Admins;
import com.example.authservice.model.OAuthProviders;
import com.example.authservice.model.UserRequest;
import com.example.authservice.model.Users;
import com.example.authservice.repository.AdminOAuthProvidersRepo;
import com.example.authservice.repository.OAuthProvidersRepo;
import com.example.authservice.service.MailService;
import com.example.authservice.service.UsersService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProcessOAuth2UsersService {

	 private final UsersService userService;
	 private final OAuthProvidersRepo OAuthRepo;
	 private final MailService mailService;
	 private final AdminOAuthProvidersRepo adminOAuthProviderRepo;
	 
	 public ProcessOAuth2UsersService(UsersService userService,OAuthProvidersRepo OAuthRepo,
			 @Lazy MailService mailservice , AdminOAuthProvidersRepo adminOAuthProviderRepo) {
		 this.userService = userService;
		 this.OAuthRepo = OAuthRepo;
		 this.mailService = mailservice;
		 this.adminOAuthProviderRepo = adminOAuthProviderRepo;
	 }
	
	 public void processOAuthUsers(String email,String name,String provider,String providerid) {
		 Users user = userService.getUserEntity(email);
         Admins admin = userService.getAdminEntity(email);
         
         if(user == null && admin == null) {
 			Users newuser = new Users();
 			newuser.setName(name);
 			newuser.setEmail(email);
 			newuser.setAddress(null);
 			newuser.setPassword(null);
 			newuser.setProfileCompleted(true);
 			Users savedUser = userService.saveUser(newuser);
 			
 			log.info("New user created with email: {} and provider: {}", email, provider);
 			
 			OAuthProviders OAuthProvider = new OAuthProviders();
 			OAuthProvider.setProvider(provider);
 			OAuthProvider.setProviderid(providerid);
 			OAuthProvider.setUserid(savedUser);
 			OAuthRepo.save(OAuthProvider);
 			
 			log.info("OAuth provider details saved for user with email: {} and provider: {}", email, provider);
 			log.info("Sending welcome email to user with email: {}", email);
 			
 			mailService.accountCreationMail(new UserRequest(savedUser.getId(),savedUser.getEmail(),savedUser.getName()));
 		}
         
         else {
        	 
        	 if(user != null) {
         		user.setName(name);
      			userService.saveUser(user);
      			
      			log.info("Existing user updated with email: {} and provider: {}", email, provider);
      			log.info("Checking if OAuth provider details already exist for user with email: {} and provider: {}", email, provider);
      			
      			OAuthProviders existProvider = OAuthRepo.findByProviderAndProviderid(provider, providerid);
      			if(existProvider == null) {
      				
      				OAuthProviders OAuthProvider = new OAuthProviders();
                      OAuthProvider.setProvider(provider);
                      OAuthProvider.setProviderid(providerid);
                      OAuthProvider.setUserid(user);

                      OAuthRepo.save(OAuthProvider);
                      log.info("OAuth provider details saved for existing user with email: {} and provider: {}", email, provider);
      			}
         	}
        	 
        	 else if(admin != null) {
        		 admin.setAdminName(name);
        		 
        		 userService.saveAdmin(admin);
        		 log.info("Existing admin updated with email: {} and provider: {}", email, provider);
        		 log.info("Checking if OAuth provider details already exist for admin with email: {} and provider: {}", email, provider);
				 
        		 AdminOAuthProviders existProvider = adminOAuthProviderRepo.findByProviderAndProviderid(provider, providerid);
				 if(existProvider == null) {
					 AdminOAuthProviders oauthprovider = new AdminOAuthProviders();
					 oauthprovider.setProvider(provider);
					 oauthprovider.setProviderid(providerid);
					 oauthprovider.setAdminid(admin);

					 adminOAuthProviderRepo.save(oauthprovider);
					 log.info("OAuth provider details saved for existing admin with email: {} and provider: {}", email, provider);
				 }
			 }
 		}
	 } 
}