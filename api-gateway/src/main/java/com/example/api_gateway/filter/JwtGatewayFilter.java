package com.example.api_gateway.filter;

import java.net.InetSocketAddress;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.example.api_gateway.jwt.JwtService;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import reactor.core.publisher.Mono;

@Component
public class JwtGatewayFilter implements GlobalFilter,Ordered { 

	private final JwtService jwtService;
	private final RateLimiterRegistry rateLimiterRegistry;
		
	public JwtGatewayFilter(JwtService jwtService ,RateLimiterRegistry rateLimiterRegistry) {
		this.jwtService = jwtService;
		this.rateLimiterRegistry = rateLimiterRegistry;
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
	    	
	    	String Key = "ip:" + getClientIpAddress(exchange);
	        return withRateLimit(exchange, chain, Key, "Public-Api");
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
	    
	    String Key = role + userId;
	    return withRateLimit(exchange.mutate().request(mutatedRequest).build(), chain, Key, "Protected-Api");
	    
	}
	
	private Mono<Void> withRateLimit(ServerWebExchange exchange, GatewayFilterChain chain, String key, String configName){
		
		RateLimiter limiter = rateLimiterRegistry.rateLimiter(key,configName);
		return chain.filter(exchange)
				.transformDeferred(RateLimiterOperator.of(limiter))
				.onErrorResume(RequestNotPermitted.class , e -> {
					exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
					exchange.getResponse().getHeaders().set("Retry-After", "1");
					return exchange.getResponse().setComplete();
				});
	}
	
	private String getClientIpAddress(ServerWebExchange exchange) {
		String forward = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
		if (forward != null && !forward.isEmpty()) return forward.split(",")[0];
		
		InetSocketAddress address = exchange.getRequest().getRemoteAddress();
		return address != null ? address.getAddress().getHostAddress() : "unknown";
	}
	
	@Override
	public int getOrder() {
	    return 100;
	}
	
}