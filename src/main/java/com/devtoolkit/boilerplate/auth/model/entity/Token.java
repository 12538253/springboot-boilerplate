package com.devtoolkit.boilerplate.auth.model.entity;

import com.devtoolkit.boilerplate.auth.model.enums.TokenType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Token {
    private Long id;
    private String token;
    private TokenType tokenType;
    private boolean expired;
    private boolean revoked;
    private String userName;

    @Builder
    public Token(Long id, String token, TokenType tokenType, boolean expired, boolean revoked, String userName) {
        this.id = id;
        this.token = token;
        this.tokenType = tokenType;
        this.expired = expired;
        this.revoked = revoked;
        this.userName = userName;
    }
}