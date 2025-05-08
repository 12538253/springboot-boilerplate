package com.devtoolkit.boilerplate.auth.repository;

import com.devtoolkit.boilerplate.auth.model.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcClient jdbcClient;

     public Optional<User> findByEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("email must not be null");
        }

        String sql = """
                    SELECT id, user_id, name, email, password
                    FROM users
                    WHERE email = :email
                """;

        return jdbcClient.sql(sql)
                .param("email", email)
                .query((rs, rowNum) -> User.builder()
                        .id(rs.getLong("id"))
                        .userId(rs.getString("user_id"))
                        .name(rs.getString("name"))
                        .email(rs.getString("email"))
                        .password(rs.getString("password"))
                        .build())
                .optional();
    }
}