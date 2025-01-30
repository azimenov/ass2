package org.example.ass2.service;

import lombok.RequiredArgsConstructor;
import org.example.ass2.model.CheckRequest;
import org.example.ass2.model.CheckResponse;
import org.example.ass2.model.Token;
import org.example.ass2.model.TokenRequest;
import org.example.ass2.repository.implementation.DefaultTokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

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

        if(tokenFromDb.getExpiresAt().before(Timestamp.from(Instant.now()))) {
            return null;
        }

        tokenRepository.getCache().put(authorizationHeader, tokenFromDb);

        return ResponseEntity.ok(CheckResponse.builder()
                .clientId(tokenFromDb.getClientId())
                .scope(tokenFromDb.getScope())
                .build());
    }
}
