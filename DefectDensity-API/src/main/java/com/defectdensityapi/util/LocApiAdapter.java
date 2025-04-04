package com.defectdensityapi.util;

import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LocApiAdapter {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${loc.api.url:}") // loading the value from the application properties, default currently it is empty
    private String locApiUrl;

    public int getTotalLinesOfCode() {
        if (locApiUrl == null || locApiUrl.isEmpty()) {
            return MockgetLinesOfCode();
        }
        try {
            ResponseEntity<Integer> response = restTemplate.getForEntity(locApiUrl, Integer.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            System.out.println("Error calling LOC API, using mock value: " + e.getMessage());
        }

        return MockgetLinesOfCode();
    }

    public int MockgetLinesOfCode() {
        return new Random().nextInt(5000) + 1000;
    }
}
