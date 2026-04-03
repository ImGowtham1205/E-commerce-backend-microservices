package com.example.authservice.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OAuth2Service extends DefaultOAuth2UserService{
		
	private final ProcessOAuth2UsersService oauthuserservice;
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest request) {
		OAuth2User oauthuser = super.loadUser(request);
		String provider = request.getClientRegistration().getRegistrationId().toUpperCase();
		String email = oauthuser.getAttribute("email");
		String name = oauthuser.getAttribute("name");
		String providerid = oauthuser.getName();
		
		oauthuserservice.processOAuthUsers(email, name, provider, providerid);
		
		return oauthuser;		
	}	
}