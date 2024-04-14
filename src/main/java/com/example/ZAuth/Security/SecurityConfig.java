package com.example.ZAuth.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable() // Disable CSRF (not recommended for production)
                .authorizeRequests()
                .anyRequest().permitAll(); // Permit all requests without authentication

        return httpSecurity.build();
    }
}
