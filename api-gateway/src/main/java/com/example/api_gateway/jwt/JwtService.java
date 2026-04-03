package com.example.api_gateway.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
	
	public String generateToken(String email,String role,long userid) {
		
		Map<String,Object> claim = new HashMap<>();
		claim.put("role", role);
		claim.put("userid",userid);
		return Jwts.builder()
				.claims()
				.add(claim)
				.subject(email)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 1000 *60 *30))
				.and()
				.signWith(getKey())
				.compact();
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
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public boolean willExpireSoon(String token, int minutes) {
        Date expiration = extractExpiration(token);
        long diff = expiration.getTime() - System.currentTimeMillis();
        return diff <= minutes * 60 * 1000;
    }

    public String refreshToken(String token) {
        String email = extractEmail(token);
        String role = extractRole(token);
        long userid = extractUserId(token);
        return generateToken(email,role,userid);
    }
    
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    
    public long extractUserId(String token) {
    	return extractClaim(token, claims -> claims.get("userid",Long.class));
    }

    public boolean isValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}