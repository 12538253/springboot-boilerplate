package com.devtoolkit.boilerplate.auth.contorller;

import com.devtoolkit.boilerplate.auth.model.dto.AuthRequest;
import com.devtoolkit.boilerplate.auth.model.dto.AuthResponse;
import com.devtoolkit.boilerplate.auth.model.entity.User;
import com.devtoolkit.boilerplate.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest login) {
        User user = User.builder()
                .email(login.email())
                .password(login.password())
                .build();
        return ResponseEntity.ok(authService.authenticate(user));
        // authority : 회원가입 , 인가
        // authentication : 로그인 , 인증 
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }
}
