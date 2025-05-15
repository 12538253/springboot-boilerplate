package com.devtoolkit.boilerplate.auth.filter;

import com.devtoolkit.boilerplate.auth.repository.TokenRepository;
import com.devtoolkit.boilerplate.auth.service.JwtService;
import com.devtoolkit.boilerplate.common.response.Response;
import com.devtoolkit.boilerplate.common.response.model.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private final ObjectMapper objectMapper;
    
    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            TokenRepository tokenRepository,
            ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.tokenRepository = tokenRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail; // username

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            sendErrorResponse(response, ResponseCode.TOKEN_EXPIRED);
            return;
        } catch (Exception e) {
            sendErrorResponse(response, ResponseCode.INVALID_TOKEN);
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            boolean isTokenValid = tokenRepository.findByToken(jwt).map(t -> !t.isExpired() && !t.isRevoked()).orElse(false);

            if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null,
                                userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                filterChain.doFilter(request, response);
            } else {
                sendErrorResponse(response, ResponseCode.INVALID_TOKEN);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
    
    /**
     * 인증 오류 응답을 전송합니다.
     * 
     * @param response HTTP 응답 객체
     * @param responseCode 응답 코드
     * @throws IOException IO 예외 발생 시
     */
    private void sendErrorResponse(HttpServletResponse response, ResponseCode responseCode) throws IOException {
        Response.Fail errorResponse = Response.Fail.of(responseCode, responseCode.message());
        
        int httpStatus = determineHttpStatus(responseCode);
        response.setStatus(httpStatus);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
    
    /**
     * ResponseCode에 따른 HTTP 상태 코드를 결정합니다.
     * 
     * @param responseCode 응답 코드
     * @return HTTP 상태 코드
     */
    private int determineHttpStatus(ResponseCode responseCode) {
        return switch (responseCode) {
            case FORBIDDEN -> HttpServletResponse.SC_FORBIDDEN; // 403
            default -> HttpServletResponse.SC_UNAUTHORIZED; // 401
        };
    }
}