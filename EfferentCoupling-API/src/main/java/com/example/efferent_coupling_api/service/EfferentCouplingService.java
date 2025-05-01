package com.example.efferent_coupling_api.service;
import com.example.efferent_coupling_api.utilities.RepoFetcher;

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
        RepoFetcher.FetchResult fetchResult = RepoFetcher.fetchRepo(repoUrl);

        if (fetchResult.error != null) {
            // If not cloned yet, return an error to the client
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", fetchResult.error);
            return ResponseEntity.ok(errorResponse);
        }


        File repoDir = new File(fetchResult.repoDir);
        Map<String, Integer> result = JavaParserUtil.computeEfferentCoupling(repoDir);


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