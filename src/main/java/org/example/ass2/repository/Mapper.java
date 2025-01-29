package org.example.ass2.repository;

import org.example.ass2.model.Token;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Mapper {

    public static Token readToken(ResultSet rs) throws SQLException {
        String clientId = rs.getString("client_id");

        if (clientId == null) {
            return null;
        }

        return Token.builder()
                .clientId(clientId.trim())
                .accessToken(rs.getString("access_token "))
                .expiresAt(rs.getTimestamp("expiration_time"))
                .build();
    }

}
