package com.devtoolkit.boilerplate.auth.config;

import com.devtoolkit.boilerplate.auth.filter.JwtAuthenticationFilter;
import com.devtoolkit.boilerplate.auth.handler.JwtLogoutHandler;
import com.devtoolkit.boilerplate.auth.repository.TokenRepository;
import com.devtoolkit.boilerplate.auth.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutHandler;


@Configuration
public class JwtSecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            TokenRepository tokenRepository,
            ObjectMapper objectMapper) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService, tokenRepository, objectMapper);
    }

    @Bean
    public LogoutHandler logoutHandler(TokenRepository tokenRepository) {
        return new JwtLogoutHandler(tokenRepository);
    }
}
