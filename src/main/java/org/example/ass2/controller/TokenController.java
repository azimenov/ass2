package org.example.ass2.controller;

import lombok.RequiredArgsConstructor;
import org.example.ass2.model.CheckResponse;
import org.example.ass2.model.TokenRequest;
import org.example.ass2.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody TokenRequest tokenRequest) {
        return tokenService.add(tokenRequest);
    }

    @GetMapping("/check")
    public ResponseEntity<CheckResponse> check(@RequestHeader String authorizationHeader) {
        return tokenService.check(authorizationHeader);
    }
}