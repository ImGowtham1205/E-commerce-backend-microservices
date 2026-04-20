package com.example.authservice.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.authservice.repository.BlackListTokenRepo;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.UsersService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter{
		
	private final JwtService jwtservice;
	private final UsersService userservice;
	private final BlackListTokenRepo blacklistrepo;
	
	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
			FilterChain filterChain) throws ServletException, IOException {

	    String path = request.getRequestURI();

	    if (path.startsWith("/oauth2") || path.startsWith("/login/oauth2")) {
	        filterChain.doFilter(request, response);
	        return;
	    }

	    String authheader = request.getHeader("Authorization");
	    String token = null;
	    String email = null;

	    if(authheader != null && authheader.startsWith("Bearer ")) {
	        token = authheader.substring(7);

	        if (blacklistrepo.existsByToken(token)) {
	            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token is invalidated");
	            return;
	        }

	        email = jwtservice.extractEmail(token);
	    }

	    if(email != null && SecurityContextHolder.getContext().getAuthentication()==null) {
	        UserDetails userdetails = userservice.loadUserByUsername(email);

	        if(jwtservice.validateToken(token,userdetails)) {
	            UsernamePasswordAuthenticationToken authtoken =
	                    new UsernamePasswordAuthenticationToken
	                    	(userdetails, token, userdetails.getAuthorities());
	            authtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	            SecurityContextHolder.getContext().setAuthentication(authtoken);
	        }
	    }
	    
	    filterChain.doFilter(request, response);
	}
}
