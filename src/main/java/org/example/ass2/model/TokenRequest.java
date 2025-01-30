package org.example.ass2.model;

import lombok.Data;

@Data
public class TokenRequest {
    private String clientId;
    private String scope;
    private String clientSecret;
    private String grantType;
}
