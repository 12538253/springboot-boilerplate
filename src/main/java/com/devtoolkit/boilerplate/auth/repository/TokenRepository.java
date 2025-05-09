package com.devtoolkit.boilerplate.auth.repository;


import com.devtoolkit.boilerplate.auth.model.entity.Token;
import com.devtoolkit.boilerplate.auth.model.enums.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

    private final JdbcClient jdbcClient;

    public List<Token> findAllValidTokenByUserId(String userName) {
        if (userName == null) {
            throw new IllegalArgumentException("userName must not be null");
        }

        String sql = """
                    SELECT id, token, token_type, expired, revoked, user_name
                    FROM tokens
                    WHERE user_name = :userName
                """;

        return jdbcClient.sql(sql)
                .param("userName", userName)
                .query((rs, rowNum) -> Token.builder()
                        .id(rs.getLong("id"))
                        .token(rs.getString("token"))
                        .tokenType(TokenType.valueOf(rs.getString("token_type")))
                        .expired(rs.getBoolean("expired"))
                        .revoked(rs.getBoolean("revoked"))
                        .userName(rs.getString("user_name"))
                        .build())
                .list();
    }

    public Optional<Token> findByToken(String token) {
        if (token == null) {
            throw new IllegalArgumentException("token must not be null");
        }

        String sql = """
                    SELECT id, token, token_type, expired, revoked, user_name
                    FROM tokens
                    WHERE token = :token
                """;

        return jdbcClient.sql(sql)
                .param("token", token)
                .query((rs, rowNum) -> Token.builder()
                        .id(rs.getLong("id"))
                        .token(rs.getString("token"))
                        .tokenType(TokenType.valueOf(rs.getString("token_type")))
                        .expired(rs.getBoolean("expired"))
                        .revoked(rs.getBoolean("revoked"))
                        .userName(rs.getString("user_name"))
                        .build())
                .optional();
    }

    public void saveToken(Token token) {
        String sql = """
                    INSERT INTO tokens (token, token_type, expired, revoked, user_name)
                    VALUES (:token, :tokenType, :expired, :revoked, :userName)
                """;

        jdbcClient.sql(sql)
                .param("token", token.getToken())
                .param("tokenType", token.getTokenType().name())
                .param("expired", token.isExpired())
                .param("revoked", token.isRevoked())
                .param("userName", token.getUserName())
                .update();
    }

    public void invalidateToken(String token) {
        String sql = """
                    UPDATE tokens
                    SET expired = true, revoked = true
                    WHERE token = :token
                """;

        jdbcClient.sql(sql)
                .param("token", token)
                .update();
    }

    public void revokeAllUserTokens(String userName) {
        String sql = """
                    UPDATE tokens
                    SET expired = true,
                        revoked = true
                    WHERE user_name = :userName
                      AND expired = false
                      AND revoked = false
                """;

        jdbcClient.sql(sql)
                .param("userName", userName)
                .update();
    }
}