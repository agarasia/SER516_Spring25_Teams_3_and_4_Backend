package com.example.efferent_coupling_api.controller;

import com.example.efferent_coupling_api.model.EfferentCouplingData;
import com.example.efferent_coupling_api.service.EfferentCouplingService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api/efferent-coupling")
public class EfferentCouplingController {
    private final EfferentCouplingService couplingService;

    public EfferentCouplingController(EfferentCouplingService couplingService) {
        this.couplingService = couplingService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeFromGitHub(@RequestParam("url") String repoUrl) throws Exception {
        return couplingService.processGitHubRepo(repoUrl);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<EfferentCouplingData>> getAllData() {
        
        return couplingService.getDB_Data();
    }

}