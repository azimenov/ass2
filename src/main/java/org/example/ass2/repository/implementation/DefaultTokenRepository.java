package org.example.ass2.repository.implementation;

import lombok.RequiredArgsConstructor;
import org.example.ass2.model.Token;
import org.example.ass2.repository.Mapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DefaultTokenRepository {

    private static final String ADD_QUERY = "INSERT INTO public.token (client_id, access_token) VALUES (?, ?)";
    private static final String GET_TOKEN_IDS_QUERY =
            "SELECT * FROM token WHERE expiration_time >= current_timestamp - interval '1.5 hours';";
    private static final String GET_TOKEN_BY_CLIENT_ID_AND_SCOPE =
            "select * from token where client_id=? and access_scope=?";

    private Map<String, Token> tokenMap = new ConcurrentHashMap<>();
    private final JdbcTemplate jdbcTemplate;

    public Map<String, Token> getCache() {
        return tokenMap;
    }

    public Token getTokenWithAccessToken(String accessToken) {
        return jdbcTemplate.queryForObject(
                "select * from token where access_token=?",
                (rs, rowNum) -> Token.builder()
                        .clientId(rs.getString("clientId"))
                        .accessToken(rs.getString("accessToken"))
                        .scope(rs.getString("scope"))
                        .expiresAt(rs.getTimestamp("expiresAt"))
                        .build()
                , accessToken);
    }

    public Token getTokenWithClientIdAndScope(String clientId, String scope) {
        return jdbcTemplate.queryForObject(
                "select * from token where client_id=? and access_scope=?",
                (rs, rowNum) -> Token.builder()
                        .clientId(rs.getString("clientId"))
                        .accessToken(rs.getString("accessToken"))
                        .scope(rs.getString("scope"))
                        .expiresAt(rs.getTimestamp("expiresAt"))
                        .build()
                , clientId, scope);
    }

    public void removeToken(String accessToken) {
        jdbcTemplate.update(
                "delete from token where access_token=?",
                accessToken
        );
    }

    public String insertAndGetToken(String clientId, String token) {
        return jdbcTemplate.queryForObject(
                "insert into token(client_id, access_scope) VALUES(?, ?) returning access_token",
                String.class, clientId, token
        );
    }

    public void refreshCache() {
        List<Token> tokens = jdbcTemplate.query(
                GET_TOKEN_IDS_QUERY,
                (rs, rn) -> Mapper.readToken(rs)
        );
        tokenMap = tokens.stream().collect(Collectors.toMap(Token::getAccessToken, Function.identity()));
    }
}