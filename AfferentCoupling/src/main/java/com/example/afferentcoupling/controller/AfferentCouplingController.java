package com.example.afferentcoupling.controller;

import com.example.afferentcoupling.dto.RepoRequest;
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
@RequestMapping("/afferent")
public class AfferentCouplingController {

    @Autowired
    private AfferentCouplingService service;

    @PostMapping
    public ResponseEntity<Map<String, Object>> computeFromGitHub(@RequestBody RepoRequest request) {

        String timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        try {
            Map<String, Integer> couplingResult = service.processGitHubRepo(request.getRepoUrl());

            // If the repo wasn't cloned or the result is null/empty, return a clean error
            if (couplingResult.size() == 1 && couplingResult.containsKey("Clone the repo first.") && couplingResult.get("Clone the repo first.") == 404) {
                Map<String, Object> errorResponse = new LinkedHashMap<>();
                errorResponse.put("error", "Clone the repo first.");
                return ResponseEntity.ok(errorResponse);
            }
            
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
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("error", ex.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }
}
