package org.example.ass2.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Token {

    private String clientId;
    private String accessToken;
    private Timestamp expiresAt;

}
