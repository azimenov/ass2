package org.example.ass2.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
public class Token {

    private String clientId;
    private String accessToken;
    private String scope;
    private Timestamp expiresAt;
}
