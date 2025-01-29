package org.example.ass2.repository.implementation;

import lombok.RequiredArgsConstructor;
import org.example.ass2.model.Token;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DefaultTokenRepository {

    private static final String ADD_QUERY = "INSERT INTO public.token (client_id, access_token) VALUES (?, ?)";

    private final JdbcTemplate jdbcTemplate;
    private final TokenCache tokenCache;

    public void add(Token token) {
        tokenCache.refreshCache();
        jdbcTemplate.update(ADD_QUERY, token.getClientId(), token.getAccessToken());
    }

    public boolean checkToken(String clientId) {
        return tokenCache.containsToken(clientId);
    }
}