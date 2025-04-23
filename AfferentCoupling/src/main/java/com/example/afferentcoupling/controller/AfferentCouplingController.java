package com.example.afferentcoupling.controller;

import com.example.afferentcoupling.service.AfferentCouplingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;


@RestController
@RequestMapping("/api")
public class AfferentCouplingController {
   
    @Autowired
    private AfferentCouplingService service;

    @PostMapping("/coupling/github")
    public ResponseEntity<Map<String, Object>> computeFromGitHub(
            @RequestParam String repoUrl,
            @RequestParam(required = false) String token) {
        Map<String, Integer> couplingResult = service.processGitHubRepo(repoUrl, token);
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
        
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : couplingResult.entrySet()) {
            Map<String, Object> classData = new LinkedHashMap<>();
            classData.put("class_name", entry.getKey());
            classData.put("score", entry.getValue());
            dataList.add(classData);
        }
        
        response.put("data", dataList);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("error", "Bad Request");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("error", "Internal Server Error");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}