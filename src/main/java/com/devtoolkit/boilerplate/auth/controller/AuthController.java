package com.devtoolkit.boilerplate.auth.controller;

import com.devtoolkit.boilerplate.auth.model.dto.AuthRequest;
import com.devtoolkit.boilerplate.auth.model.dto.AuthResponse;
import com.devtoolkit.boilerplate.auth.model.entity.User;
import com.devtoolkit.boilerplate.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth/login")
    public AuthResponse authenticate(@RequestBody AuthRequest login) {
        User user = User.builder()
                .email(login.email())
                .password(login.password())
                .build();
        
        return authService.authenticate(user);
    }

    @PostMapping("/auth/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }

    @GetMapping("/me")
    public Map<String, Object> hello(Authentication authentication) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", authentication.getName());
        return attributes;
    }
}
