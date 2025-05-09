package com.devtoolkit.boilerplate.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.devtoolkit.boilerplate.auth.model.dto.AuthResponse;
import com.devtoolkit.boilerplate.auth.model.enums.TokenType;
import com.devtoolkit.boilerplate.auth.model.entity.Token;
import com.devtoolkit.boilerplate.auth.repository.TokenRepository;
import com.devtoolkit.boilerplate.auth.model.entity.User;
import com.devtoolkit.boilerplate.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public AuthResponse authenticate(User user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveToken(user, jwtToken);
        return new AuthResponse(jwtToken, refreshToken);
    }

    private void revokeAllUserTokens(User user) {
        tokenRepository.revokeAllUserTokens(user.getUsername());
    }

    private void saveToken(User user, String jwtToken) {
        Token token = Token.builder()
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .userName(user.getUsername())
                .build();
        tokenRepository.saveToken(token);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail; // username
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            User user = userRepository.findByEmail(userEmail).orElseThrow();

            // 리프레시 토큰이 유효한 경우에만 처리
            if (jwtService.isTokenValid(refreshToken, user)) {
                // 기존 토큰 무효화
                tokenRepository.invalidateToken(refreshToken);

                // 새 Access Token 생성 및 저장
                String newAccessToken = jwtService.generateToken(user);
                saveToken(user, newAccessToken);

                AuthResponse authResponse = new AuthResponse(newAccessToken, refreshToken);
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

}
