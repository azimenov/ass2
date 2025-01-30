package org.example.ass2.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CheckResponse {
    public String clientId;
    public String scope;
}
