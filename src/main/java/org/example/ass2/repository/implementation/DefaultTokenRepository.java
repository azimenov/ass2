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

    public void add(Token token) {
        refreshCache();
        jdbcTemplate.update(ADD_QUERY, token.getClientId(), token.getAccessToken());
    }

    private void refreshCache() {
        List<Token> tokens = jdbcTemplate.query(
                GET_TOKEN_IDS_QUERY,
                (rs, rn) -> Mapper.readToken(rs)
        );
        tokenMap = tokens.stream().collect(Collectors.toMap(Token::getAccessToken, Function.identity()));
    }

    public boolean containsInCache(String clientId) {
        return tokenMap.containsKey(clientId);
    }

    public String getTokenFromCache(String clientId) {
        return tokenMap.get(clientId).getAccessToken();
    }

    public Token getToken(String clientId, String scope) {
        return jdbcTemplate.query(
                GET_TOKEN_BY_CLIENT_ID_AND_SCOPE,
                (rs, rowNum) -> Token.builder()
                        .clientId(rs.getString("clientId"))
                        .accessToken(rs.getString("accessToken"))
                        .scope(rs.getString("scope"))
                        .expiresAt(rs.getTimestamp("expiresAt"))
                        .build()
                , clientId, scope).get(0);
    }

    public void deleteToken(String accessToken) {
        jdbcTemplate.update(
                "delete from token where access_token=?",
                accessToken
        );
    }

    public String getNewToken(String clientId, String scope) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO token (client_id, access_scope) VALUES (?, ?) RETURNING access_token",
                String.class,
                clientId, scope
        );
    }

    public boolean checkToken(String accessToken) {
        return tokenMap.containsKey(accessToken);
    }
}