package com.example.authservice.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.authservice.service.BlackListTokenService;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.UsersService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter{
		
	private final JwtService jwtService;
	private final UsersService userService;
	private final BlackListTokenService blackListService;
	
	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
			FilterChain filterChain) throws ServletException, IOException {

	    String path = request.getRequestURI();
	    log.info("Request Path in Auth Service : {}", path);
	    
	    if (path.startsWith("/oauth2") || path.startsWith("/login/oauth2")) {
	        filterChain.doFilter(request, response);
	        return;
	    }

	    String authheader = request.getHeader("Authorization");
	    String token = null;
	    String email = null;

	    if(authheader != null && authheader.startsWith("Bearer ")) {
	        token = authheader.substring(7);

	        if (blackListService.isBlackListed(token)) {
	        	log.error("Token is blacklisted in Auth Service: {}", token);
	            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token is invalidated");
	            return;
	        }

	        email = jwtService.extractEmail(token);
	    }

	    if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	        UserDetails userdetails = userService.loadUserByUsername(email);

	        if(jwtService.validateToken(token,userdetails)) {
	        	log.info("Token is valid for email in Auth Service: {}", email);
	            UsernamePasswordAuthenticationToken authtoken =
	                    new UsernamePasswordAuthenticationToken
	                    	(userdetails, token, userdetails.getAuthorities());
	            authtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	            SecurityContextHolder.getContext().setAuthentication(authtoken);
	            log.info("Authentication set for email in Auth Service: {}", email);
	        }
	    }
	    log.info("Proceeding with filter chain for path in Auth Service: {}", path);
	    filterChain.doFilter(request, response);
	}
}
