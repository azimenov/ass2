package org.example.ass2.service;

import lombok.RequiredArgsConstructor;
import org.example.ass2.model.*;
import org.example.ass2.repository.implementation.DefaultTokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final DefaultTokenRepository tokenRepository;

    public ResponseEntity<CheckResponse> check(String authorizationHeader) {
        Token tokenFromCache = tokenRepository.getCache().get(authorizationHeader);

        if (tokenFromCache != null && tokenFromCache.getExpiresAt().after(Timestamp.from(Instant.now()))) {
            return ResponseEntity.ok(CheckResponse.builder()
                    .clientId(tokenFromCache.getClientId())
                    .scope(tokenFromCache.getScope())
                    .build());
        }

        tokenRepository.getCache().remove(authorizationHeader);

        Token tokenFromDb = tokenRepository.getTokenWithAccessToken(authorizationHeader);

        if (tokenFromDb.getExpiresAt().before(Timestamp.from(Instant.now()))) {
            return null;
        }

        tokenRepository.getCache().put(authorizationHeader, tokenFromDb);

        return ResponseEntity.ok(CheckResponse.builder()
                .clientId(tokenFromDb.getClientId())
                .scope(tokenFromDb.getScope())
                .build());
    }

    public ResponseEntity<TokenResponse> addToken(TokenRequest tokenRequest) {
        for (Map.Entry<String, Token> entry : tokenRepository.getCache().entrySet()) {
            if (entry.getValue().getClientId().equals(tokenRequest.getClientId())
                    && entry.getValue().getScope().equals(tokenRequest.getScope())
            ) {
                return ResponseEntity.ok(
                        TokenResponse.builder()
                                .accessToken(entry.getValue().getAccessToken())
                                .build()
                );
            }
        }

        String token = getToken(tokenRequest.getClientId(), tokenRequest.getScope());
        if (token != null) {
            return ResponseEntity.ok(
                    TokenResponse.builder()
                            .accessToken(token)
                            .build()
            );
        }

        token = tokenRepository.insertAndGetToken(tokenRequest.getClientId(), token);
        tokenRepository.refreshCache();
        return ResponseEntity.ok(
                TokenResponse.builder()
                        .accessToken(token)
                        .build()
        );
    }

    private String getToken(String clientId, String scope) {
        Token tokenFromDb = tokenRepository.getTokenWithClientIdAndScope(clientId, scope);
        if (tokenFromDb.getExpiresAt().before(Timestamp.from(Instant.now()))) {
            tokenRepository.removeToken(tokenFromDb.getAccessToken());
            return "";
        }
        return tokenFromDb.getAccessToken();
    }
}
