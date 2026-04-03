package com.example.authservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.authservice.filter.JwtFilter;
import com.example.authservice.oauth.OAuth2Service;
import com.example.authservice.oauth.OAuthSuccessHandler;
import com.example.authservice.oauth.ProcessOAuth2UsersService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
	
    private final UserDetailsService user;
    private final JwtFilter filter;
    private final OAuthSuccessHandler OAuthSuccess;
    private final OAuth2Service OAuthService;
    private final ProcessOAuth2UsersService oauthuserservice;
        
    @Bean
    public SecurityFilterChain securityChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\":\"Invalid email or password\"}");
                        })
                    )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login","/auth/register",
                        		"/auth/forgot-password","/auth/reset-password",
                        		"/oauth2/**",
                        	    "/login/oauth2/**").permitAll()
                        .requestMatchers("/auth/api/user/**").hasAuthority("ROLE_USER")
                        .requestMatchers("/auth/api/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/auth/api/gettokens**").hasAnyAuthority("ROLE_USER","ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(user -> user.userService(OAuthService)
                        .oidcUserService(oidcUserService()))
                        .successHandler(OAuthSuccess)
                )
                .formLogin(form -> form.disable())
                .httpBasic(httpreq -> httpreq.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder encorder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SimpleMailMessage mailMessage() {
        return new SimpleMailMessage();
    }

    @Bean
    public DaoAuthenticationProvider provider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(user);
        provider.setPasswordEncoder(encorder());
        return provider;
    }

    @Bean
    public AuthenticationManager authentication(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {

        OidcUserService delegate = new OidcUserService();

        return userRequest -> {

            OidcUser oidcUser = delegate.loadUser(userRequest);

            String email = oidcUser.getEmail();
            String name = oidcUser.getFullName();
            String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
            String providerid = oidcUser.getSubject();
            
           oauthuserservice.processOAuthUsers(email, name, provider, providerid);
            
            return oidcUser;
        };
    } 
}
