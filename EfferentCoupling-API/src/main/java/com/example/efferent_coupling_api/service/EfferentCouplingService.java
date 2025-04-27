package com.example.efferent_coupling_api.service;

import com.example.efferent_coupling_api.util.JavaParserUtil;
import com.example.efferent_coupling_api.util.FetchRepoPythonCaller;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EfferentCouplingService {
    private static final String CLONE_DIR = "cloned-repos/";

    public ResponseEntity<Map<String, Object>> processGitHubRepo(String repoUrl) throws Exception {
        FetchRepoPythonCaller.FetchRepoResult fetchResult = FetchRepoPythonCaller.fetchRepo(repoUrl);
        File clonedRepo = new File(fetchResult.repoPath);

        Map<String, Integer> result = JavaParserUtil.computeEfferentCoupling(clonedRepo);


        Map<String, Object> responseMap = new HashMap<>();

        List<Map<String, Object>> currentEfferentPayload = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : result.entrySet()) {
            Map<String, Object> scoreMap = new HashMap<>();
            scoreMap.put("class_name", entry.getKey());
            scoreMap.put("score", entry.getValue());
            currentEfferentPayload.add(scoreMap);
        }

        Map<String, Object> EfferentcouplingMap = new LinkedHashMap<>();
        EfferentcouplingMap.put("timestamp", Instant.now().toString());
        EfferentcouplingMap.put("data", currentEfferentPayload);


        responseMap.put("current_efferent", EfferentcouplingMap);

        // System.out.println(responseMap);
        return ResponseEntity.ok(responseMap);

    }
}