package org.example.ass2.controller;

import lombok.RequiredArgsConstructor;
import org.example.ass2.model.CheckRequest;
import org.example.ass2.model.TokenRequest;
import org.example.ass2.service.TokenService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/token")
    public void token(@RequestBody TokenRequest tokenRequest) {
        tokenService.add(tokenRequest);
    }

    @GetMapping("/check")
    public boolean check(@RequestBody CheckRequest checkRequest) {
        return tokenService.check(checkRequest);
    }

}
