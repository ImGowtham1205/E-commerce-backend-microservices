package com.example.comment.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.comment.jwt.JwtService;
import com.example.comment.service.UserMicroServiceCall;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserMicroServiceCall userService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
    	
    	String path = request.getRequestURI();
    	log.info("Incoming request path in Comment Service : {}", path);
    	
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        	log.warn("Authorization header is missing or does not start with 'Bearer '. Proceeding without authentication in Comment service.");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
   
        List<String> tokens = userService.getAllBlackListTokens(authHeader);
        if(tokens != null && tokens.contains(token)) {
        	log.warn("Token is blacklisted. Proceeding without authentication in Comment service.");
        	response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
            return;
        }
        
        try {
            if (!jwtService.validateToken(token)) {
            	log.warn("Token validation failed. Proceeding without authentication in Comment service.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
                return;
            }

            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);
            
            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(role);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email,token,List.of(authority));
            
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            log.info("Authentication successful for user in Comment service: {}", email);
               
        } catch (Exception e) {
        	log.error("Error during JWT processing in Comment service: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Error: " + e.getMessage());
            return;
        }
        log.info("Proceeding with the filter chain in Comment service.");
        filterChain.doFilter(request, response);
    }
}