package com.example.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.example.api_gateway.jwt.JwtService;

import reactor.core.publisher.Mono;

@Component
public class JwtGatewayFilter implements GlobalFilter,Ordered { 

	private final JwtService jwtService;

	public JwtGatewayFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

	    String path = exchange.getRequest().getURI().getPath();
	    
	    if (path.startsWith("/oauth2") || path.startsWith("/login/oauth2") ||
	        path.contains("/auth/login") ||
	        path.contains("/auth/register") ||
	        path.contains("/auth/forgot-password") ||
	        path.contains("/auth/reset-password") ||
	        path.startsWith("/api/products/image")) {

	        return chain.filter(exchange);
	    }

	    String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
	        return exchange.getResponse().setComplete();
	    }

	    String token = authHeader.substring(7);

	    if (!jwtService.isValid(token)) {
	        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
	        return exchange.getResponse().setComplete();
	    }

	    String finalToken = token;

	    if (exchange.getAttribute("REFRESH_DONE") == null &&
	        jwtService.willExpireSoon(token, 1)) {
	    	
	        finalToken = jwtService.refreshToken(token);

	        exchange.getResponse().getHeaders()
	                .set("X-Auth-Token", "Bearer " + finalToken);

	        exchange.getAttributes().put("REFRESH_DONE", true);
	    }

	    Long userId = jwtService.extractUserId(finalToken);
	    String role = jwtService.extractRole(finalToken);

	    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
	            .header("Authorization", "Bearer " + finalToken)
	            .header("X-User-Id", String.valueOf(userId))
	            .header("X-User-Role", role)
	            .build();

	    return chain.filter(exchange.mutate().request(mutatedRequest).build());
	}

	@Override
	public int getOrder() {
	    return 100;
	}
}