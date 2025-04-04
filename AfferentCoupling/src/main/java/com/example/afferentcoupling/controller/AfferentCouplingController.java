package com.example.afferentcoupling.controller;

import com.example.afferentcoupling.model.AfferentCouplingData;
import com.example.afferentcoupling.model.CouplingRequest;
import com.example.afferentcoupling.service.AfferentCouplingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class AfferentCouplingController {

    @Autowired
    private AfferentCouplingService service;

    @GetMapping("/coupling")
    public ResponseEntity<AfferentCouplingData> getCoupling(@RequestParam String repoUrl) {
        AfferentCouplingData data = service.getCouplingData(repoUrl);
        if (data == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(data);
    }

    @PostMapping("/coupling")
    public ResponseEntity<String> postCoupling(@RequestBody CouplingRequest req) {
        service.saveCouplingData(req.getRepoUrl(), req.getCouplingData());
        return ResponseEntity.ok("Saved successfully");
    }

    @PostMapping("/coupling/github")
    public ResponseEntity<Map<String, Integer>> computeFromGitHub(@RequestParam String repoUrl, @RequestParam(required = false) String token) {
        Map<String, Integer> result = service.processGitHubRepo(repoUrl, token);
        if (result == null || result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        service.saveCouplingData(repoUrl, result);
        return ResponseEntity.ok(result);
    }
}
