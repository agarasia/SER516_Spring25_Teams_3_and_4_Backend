package com.example.efferent_coupling_api.service;

import com.example.efferent_coupling_api.util.JavaParserUtil;
// import com.example.efferent_coupling_api.model.ClassScoreModel;
// import com.example.efferent_coupling_api.model.EfferentCouplingData;
// import com.example.efferent_coupling_api.repository.EfferentCouplingRepository;
import com.example.efferent_coupling_api.util.GitCloner;

// import org.springframework.beans.factory.annotation.Autowired;
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
    // private final EfferentCouplingRepository historyRepository;

    // public EfferentCouplingService(EfferentCouplingRepository historyRepository){
    //     this.historyRepository = historyRepository;
    // }

    // @Autowired
    // private EfferentCouplingRepository repository;

    public ResponseEntity<Map<String, Object>> processGitHubRepo(String repoUrl) throws Exception {
        File clonedRepo = GitCloner.cloneRepo(repoUrl, CLONE_DIR);
        Map<String, Integer> result = JavaParserUtil.computeEfferentCoupling(clonedRepo);

        Map<String, Object> responseMap = new HashMap<>();

        List<Map<String, Object>> currentEfferentPayload = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : result.entrySet()) {
            Map<String, Object> scoreMap = new HashMap<>();
            scoreMap.put("class_name", entry.getKey());
            scoreMap.put("score", entry.getValue());
            currentEfferentPayload.add(scoreMap);
        }


        // List<EfferentCouplingData> historyList = historyRepository.findByRepoUrl(repoUrl);
        // responseMap.put("efferent_history", historyList);
        // Store to MongoDB
        // EfferentCouplingData data = new EfferentCouplingData();
        // data.setRepoUrl(repoUrl);
        // data.setCouplingData(currentEfferentPayload);
        // data.setTimestamp(Instant.now().toString());

        // repository.save(data);
        Map<String, Object> EfferentcouplingMap = new LinkedHashMap<>();
        EfferentcouplingMap.put("timestamp", Instant.now().toString());
        EfferentcouplingMap.put("data", currentEfferentPayload);


        responseMap.put("current_efferent", EfferentcouplingMap);

        System.out.println(responseMap);
        return ResponseEntity.ok(responseMap);

    }

    // public ResponseEntity<List<EfferentCouplingData>> getDB_Data() {
    //     List<EfferentCouplingData> allData = repository.findAll();
    //     return ResponseEntity.ok(allData);
    // }
}