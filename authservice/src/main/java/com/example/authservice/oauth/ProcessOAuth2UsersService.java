package com.example.authservice.oauth;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.example.authservice.model.OAuthProviders;
import com.example.authservice.model.Users;
import com.example.authservice.repository.OAuthProvidersRepo;
import com.example.authservice.service.MailService;
import com.example.authservice.service.UsersService;

@Service
public class ProcessOAuth2UsersService {

	 private final UsersService userService;
	 private final OAuthProvidersRepo oauthrepo;
	 private final MailService mailservice;
	 
	 public ProcessOAuth2UsersService(UsersService userService,OAuthProvidersRepo oauthrepo,
			 @Lazy MailService mailservice) {
		 this.userService = userService;
		 this.oauthrepo = oauthrepo;
		 this.mailservice = mailservice;
	 }
	
	 public void processOAuthUsers(String email,String name,String provider,String providerid) {
		 Users user = userService.getUserEntity(email);
         
         if(user == null) {
 			Users newuser = new Users();
 			newuser.setName(name);
 			newuser.setEmail(email);
 			newuser.setAddress(null);
 			newuser.setPassword(null);
 			newuser.setProfileCompleted(true);
 			Users saveduser = userService.saveUser(newuser);
 			
 			OAuthProviders oauthprovider = new OAuthProviders();
 			oauthprovider.setProvider(provider);
 			oauthprovider.setProviderid(providerid);
 			oauthprovider.setUserid(saveduser);
 			oauthrepo.save(oauthprovider);
 			mailservice.accountCreationMail(saveduser);
 		}
         
         else {
 			user.setName(name);
 			userService.saveUser(user);
 			
 			OAuthProviders existprovider = oauthrepo.findByProviderAndProviderid(provider, providerid);
 			if(existprovider == null) {
 				
 				OAuthProviders oauthprovider = new OAuthProviders();
                 oauthprovider.setProvider(provider);
                 oauthprovider.setProviderid(providerid);
                 oauthprovider.setUserid(user);

                 oauthrepo.save(oauthprovider);
 			}
 		}
	 } 
}