package com.example.authservice.oauth;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import com.example.authservice.model.Users;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.UsersService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler{

	private final JwtService jwtservice;
	private final UsersService userservice;
	
	private static final String FRONTEND_URL = "http://localhost:5173";
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		OAuth2User oauthuser = (OAuth2User) authentication.getPrincipal();
		String email = oauthuser.getAttribute("email");
		Users user = userservice.getUser(email);
		String role = "ROLE_USER";
		String token = jwtservice.generateToken(email, role ,user.getId());
		
		String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
		
		response.sendRedirect(FRONTEND_URL + "/oauth-success?token=" + encodedToken + "&role=" + role);	
	}
}
