package org.example.ass2.repository.implementation;

import lombok.RequiredArgsConstructor;
import org.example.ass2.model.Token;
import org.example.ass2.repository.Mapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TokenCache {

    private static final String GET_TOKEN_IDS_QUERY =
            "SELECT * FROM token WHERE expiration_time >= current_timestamp - interval '1.5 hours';";

    private final JdbcTemplate jdbcTemplate;
    private volatile Map<String, Token> tokenMap = new HashMap<>();

    public void refreshCache() {
        List<Token> tokens = jdbcTemplate.query(
                GET_TOKEN_IDS_QUERY,
                (rs, rn) -> Mapper.readToken(rs)
        );
        tokenMap = tokens.stream().collect(Collectors.toMap(Token::getClientId, Function.identity()));
    }

    public boolean containsToken(String clientId) {
        return tokenMap.containsKey(clientId);
    }
}