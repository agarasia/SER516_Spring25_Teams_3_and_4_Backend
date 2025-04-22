package com.example.afferentcoupling.controller;

import com.example.afferentcoupling.service.AfferentCouplingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AfferentCouplingController {
   
    @Autowired
    private AfferentCouplingService service;

    @PostMapping("/coupling/github")
    public ResponseEntity<Map<String, Integer>> computeFromGitHub(
            @RequestParam String repoUrl,
            @RequestParam(required = false) String token) {
        Map<String, Integer> result = service.processGitHubRepo(repoUrl, token);
        return ResponseEntity.ok(result);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Bad Request");
        response.put("message", ex.getMessage());
        response.put("timestamp", Instant.now().toString());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}