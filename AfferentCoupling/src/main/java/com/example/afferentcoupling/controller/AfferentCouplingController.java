package com.example.afferentcoupling.controller;

import com.example.afferentcoupling.service.AfferentCouplingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AfferentCouplingController {

    @Autowired
    private AfferentCouplingService service;

    @PostMapping("/coupling/github")
    public ResponseEntity<Map<String, Object>> computeFromGitHub(
            @RequestParam String repoUrl,
            @RequestParam(required = false) String token) {

        String timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        try {
            // Call single-arg if no token, otherwise remote clone path
            Map<String, Integer> couplingResult =
                    (token == null || token.isBlank())
                            ? service.processGitHubRepo(repoUrl)
                            : service.processGitHubRepo(repoUrl, token);

            // Build successful response
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("timestamp", timestamp);
            
            List<Map<String, Object>> dataList = couplingResult.entrySet().stream()
                    .map(e -> {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("class_name", e.getKey());
                        m.put("score", e.getValue());
                        return m;
                    })
                    .collect(Collectors.toList());

            response.put("data", dataList);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            // RepoFetcher.fetchRepo() returned an error → return 200 with message
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("timestamp", timestamp);
            errorResponse.put("message", ex.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }
}
