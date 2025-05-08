package com.devtoolkit.boilerplate.home;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/me")
    public ResponseEntity<String> hello(Authentication authentication) {
        logger.info("!!!!!!!!!! HomeController.hello() ABSOLUTELY ENTERED !!!!!!!!!!"); // 강조된 첫 번째 로그

        // 기존 로그: 메소드 시작 로그
        logger.info("HomeController.hello() entered. Authentication principal: {}", (authentication != null ? authentication.getName() : "null"));

        try {
            if (authentication == null || authentication.getName() == null) {
                logger.error("Authentication object or principal name is null.");
                return ResponseEntity.status(500).body("Error: Authentication information is missing.");
            }

            String userName = authentication.getName();
            String responseBody = "현재 로그인한 사용자: " + userName;

            logger.info("HomeController.hello() preparing to return: {}", responseBody);
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            logger.error("Error in HomeController.hello(): {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("An unexpected error occurred.");
        }
    }
}
