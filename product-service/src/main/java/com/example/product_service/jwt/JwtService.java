package com.example.product_service.jwt;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {
	
	@Value("${jwt.secret}")
	private String secretkey;
	
	private SecretKey key;
	
	@PostConstruct
	public void init() {
	    byte[] decoded = Decoders.BASE64.decode(secretkey);
	    key = Keys.hmacShaKeyFor(decoded);
	}
	
	private SecretKey getKey() {
		 return key;
	}
	
	public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
		
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    public boolean validateToken(String token) {
        return !extractAllClaims(token).getExpiration().before(new java.util.Date());
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    
    public long extractUserId(String token) {
    	return extractClaim(token, claims -> claims.get("userid",Long.class));
    }
    
}