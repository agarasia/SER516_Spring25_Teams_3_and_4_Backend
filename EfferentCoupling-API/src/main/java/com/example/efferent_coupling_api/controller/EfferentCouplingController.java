package com.example.efferent_coupling_api.controller;

import com.example.efferent_coupling_api.service.EfferentCouplingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;


@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api/efferent-coupling")
public class EfferentCouplingController {
    private final EfferentCouplingService couplingService;

    public EfferentCouplingController(EfferentCouplingService couplingService) {
        this.couplingService = couplingService;
    }

    @PostMapping("/analyze")
    public Map<String, Integer> analyzeFromGitHub(@RequestParam("url") String repoUrl) throws Exception {
        return couplingService.processGitHubRepo(repoUrl);
    }
}