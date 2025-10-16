package com.codehunter.hotelbooking.ai.copilot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Profile("openai")
@Component
@Slf4j
public class CopilotTokenService {
    @Value("${app.ai.copilot.openai.key}")
    private String copilotKey;

    private final RestClient restClient = RestClient.create();
    private final Map<String, CopilotTokenResponse> tokenCache = new ConcurrentHashMap<>();
    private static final String CACHE_KEY = "default";

    public String getToken() {
        CopilotTokenResponse cached = tokenCache.get(CACHE_KEY);
        long nowEpoch = Instant.now().getEpochSecond();
        if (isTokenValid(cached, nowEpoch)) {
            log.info("loading copilot token from cache");
            return cached.getToken();
        }
        log.info("fetching new copilot token from API");
        CopilotTokenResponse response = fetchTokenFromApi();
        tokenCache.put(CACHE_KEY, response);
        return response.getToken();
    }

    private boolean isTokenValid(CopilotTokenResponse cached, long nowEpoch) {
        if (cached == null || cached.getExpiresAt() == null) return false;
        try {
            long expiresEpoch = Long.parseLong(cached.getExpiresAt());
            return expiresEpoch > nowEpoch;
        } catch (Exception e) {
            log.error("Error parsing token expiration", e);
            return false;
        }
    }

    private CopilotTokenResponse fetchTokenFromApi() {
        String url = "https://api.github.com/copilot_internal/v2/token";
        try {
            CopilotTokenResponse response = restClient.get()
                    .uri(url)
                    .header("Authorization", "Bearer " + copilotKey)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(CopilotTokenResponse.class);
            log.info("get copilot token response: {}", response);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map response to CopilotTokenResponse", e);
        }
    }
}
