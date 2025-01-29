package org.example.ass2.service;

import lombok.RequiredArgsConstructor;
import org.example.ass2.model.CheckRequest;
import org.example.ass2.model.Token;
import org.example.ass2.model.TokenRequest;
import org.example.ass2.repository.implementation.DefaultTokenRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final DefaultTokenRepository tokenRepository;

    public void add(TokenRequest tokenRequest) {
        tokenRepository.add(
                Token.builder()
                        .clientId(tokenRequest.getClientId())
                        .accessToken(tokenRequest.getClientSecret())
                        .expiresAt(Timestamp.from(Instant.now()))
                        .build()
        );
    }

    public boolean check(CheckRequest checkRequest) {
        return tokenRepository.checkToken(checkRequest.getToken());
    }

}
