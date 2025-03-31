package com.example.afferentcoupling.controller;

import com.example.afferentcoupling.service.AfferentCouplingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AfferentCouplingController {

    @Autowired
    private AfferentCouplingService service;

    @PostMapping("/coupling/github")
    public ResponseEntity<Map<String, Integer>> computeFromGitHub(@RequestParam String repoUrl) {
        Map<String, Integer> result = service.processGitHubRepo(repoUrl);
        return ResponseEntity.ok(result);
    }
}
