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

    public ResponseEntity<?> add(TokenRequest tokenRequest) {
        if (tokenRepository.containsInCache(tokenRequest.getClientId())) {
            return ResponseEntity.ok(tokenRepository.getTokenFromCache(tokenRequest.getClientId()));
        }

        String token = getToken(tokenRequest.getClientId(), tokenRequest.getScope());

        return ResponseEntity.ok(token);
    }

    public ResponseEntity<CheckResponse> check(String accessToken) {
        if (tokenRepository.checkToken(accessToken)) {
            Token token = tokenRepository.getTokenFromCache(accessToken);
            if()
        }
    }

    private String getToken(String clientId, String scope) {
        Token token = tokenRepository.getToken(clientId, scope);

        if (token.getExpiresAt().before(Timestamp.from(Instant.now()))) {
            tokenRepository.deleteToken(token.getAccessToken());
            token.setAccessToken(tokenRepository.getNewToken(clientId, scope));
        }

        return token.getAccessToken();
    }
}
