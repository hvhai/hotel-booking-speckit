package com.codehunter.hotelbooking.ai.copilot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CopilotTokenResponse {
    private String token;
    @JsonProperty("expires_at")
    private String expiresAt;
}

