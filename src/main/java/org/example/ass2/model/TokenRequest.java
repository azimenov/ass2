package org.example.ass2.model;

import lombok.Data;

import java.util.List;

@Data
public class TokenRequest {
    private String clientId;
    private String clientSecret;
    private List<String> scopes;
}
