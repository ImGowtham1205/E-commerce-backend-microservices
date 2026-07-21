package com.example.comment.configuration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.openfeign.support.FeignHttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import feign.RequestInterceptor;
import feign.codec.Decoder;

@Configuration
public class FeignConfig {

	@Bean
	RequestInterceptor requestInterceptor() {
	    return requestTemplate -> {
	        var context = SecurityContextHolder.getContext();
	          
	        if (context.getAuthentication() != null) {
	            Object credentials = context.getAuthentication().getCredentials();
	          
	            if (credentials instanceof String token) {
	                requestTemplate.header("Authorization", "Bearer " + token);
	            }
	        }
	    };
	}
	
	   @Bean
	    Decoder feignDecoder(ObjectProvider<FeignHttpMessageConverters> messageConverters) {
	        return new ResponseEntityDecoder(new SpringDecoder(messageConverters));
	    }
}