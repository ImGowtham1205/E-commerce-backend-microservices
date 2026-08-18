package com.example.authservice.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OAuth2Service extends DefaultOAuth2UserService{
		
	private final ProcessOAuth2UsersService OAuthUserService;
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest request) {
		OAuth2User OAuthUser = super.loadUser(request);
		String provider = request.getClientRegistration().getRegistrationId().toUpperCase();
		String email = OAuthUser.getAttribute("email");
		String name = OAuthUser.getAttribute("name");
		String providerid = OAuthUser.getName();
		
		OAuthUserService.processOAuthUsers(email, name, provider, providerid);
		
		return OAuthUser;		
	}	
}