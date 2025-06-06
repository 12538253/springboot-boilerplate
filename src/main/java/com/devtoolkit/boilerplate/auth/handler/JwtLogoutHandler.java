package com.devtoolkit.boilerplate.auth.handler;

import com.devtoolkit.boilerplate.auth.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;


public class JwtLogoutHandler implements LogoutHandler {
    private final TokenRepository tokenRepository;
    
    public JwtLogoutHandler(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return;

        final String jwt = authHeader.substring(7);
        tokenRepository.findByToken(jwt)
                .ifPresent(storedToken -> tokenRepository.invalidateToken(jwt));
    }
}
