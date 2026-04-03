package com.example.api_gateway.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
	    return http
	    	.cors(cors->cors.configurationSource(corsConfigurationSource()))
	        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
	        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
	        .csrf(ServerHttpSecurity.CsrfSpec::disable)
	        .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
	        .authorizeExchange(exchange -> exchange

	            .pathMatchers("/api/products/image/**").permitAll()
	            .pathMatchers("/authservice/auth/login","/authservice/auth/register",
	            		"/authservice/auth/forgot-password","/authservice/auth/reset-password").permitAll()
	            .pathMatchers("/authservice/oauth2/**","/authservice/login/oauth2/**").permitAll()
	            .anyExchange().permitAll()
	        )
	        .build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:5173"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setExposedHeaders(List.of("Authorization", "X-Auth-Token"));
		config.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}