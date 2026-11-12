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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class JwtGatewayFilter implements GlobalFilter,Ordered { 

	private final JwtService jwtService;
	private final RateLimiterRegistry rateLimiterRegistry;
		
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

	    String path = exchange.getRequest().getURI().getPath();
	    log.info("Request path in API Gateway: {}", path);
	    if (path.startsWith("/oauth2") || path.startsWith("/login/oauth2") ||
	        path.contains("/auth/login") || path.contains("/auth/register") ||
	        path.contains("/auth/forgot-password") || path.contains("/auth/reset-password") ||
	        path.startsWith("/api/products/image") ||  path.startsWith("/actuator")) {
	    	
	    	String Key = "ip:" + getClientIpAddress(exchange);
	    	log.info("Public API request from IP in API Gateway: {}", Key);
	        return withRateLimit(exchange, chain, Key, "Public-Api");
	    }

	    String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
	        log.warn("Unauthorized request: Missing or invalid Authorization header in API Gateway");
	        return exchange.getResponse().setComplete();
	    }

	    String token = authHeader.substring(7);

	    if (!jwtService.isValid(token)) {
	        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
	        log.warn("Unauthorized request: Invalid Or Expired JWT token in API Gateway");
	        return exchange.getResponse().setComplete();
	    }

	    String finalToken = token;

	    if (exchange.getAttribute("REFRESH_DONE") == null &&
	        jwtService.willExpireSoon(token, 1)) {
	        finalToken = jwtService.refreshToken(token);
	        exchange.getResponse().getHeaders().set("X-Auth-Token", "Bearer " + finalToken);
	        exchange.getAttributes().put("REFRESH_DONE", true);
	        log.info("JWT token refreshed for Id and role in API Gateway: {}", jwtService.extractUserId(finalToken), jwtService.extractRole(finalToken));
	    }

	    Long userId = jwtService.extractUserId(finalToken);
	    String role = jwtService.extractRole(finalToken);

	    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
	            .header("Authorization", "Bearer " + finalToken)
	            .header("X-User-Id", String.valueOf(userId))
	            .header("X-User-Role", role)
	            .build();
	    
	    String Key = role + userId;
	    log.info("Protected API request from userId: {} with role in API Gateway: {}", userId, role);
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