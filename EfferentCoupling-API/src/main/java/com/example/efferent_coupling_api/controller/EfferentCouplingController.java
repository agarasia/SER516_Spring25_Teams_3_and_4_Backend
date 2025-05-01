package com.example.efferent_coupling_api.controller;

import com.example.efferent_coupling_api.service.EfferentCouplingService;
import com.example.efferent_coupling_api.dto.RepoRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/efferent")
public class EfferentCouplingController {

    private final EfferentCouplingService couplingService;

    public EfferentCouplingController(EfferentCouplingService couplingService) {
        this.couplingService = couplingService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> analyzeFromGitHub(@RequestBody RepoRequest request) throws Exception {
        return couplingService.processGitHubRepo(request.getRepoUrl());
    }
}
