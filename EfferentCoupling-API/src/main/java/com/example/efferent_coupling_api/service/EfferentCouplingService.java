package com.example.efferent_coupling_api.service;

import com.example.efferent_coupling_api.util.JavaParserUtil;
import com.example.efferent_coupling_api.model.EfferentCouplingData;
import com.example.efferent_coupling_api.repository.EfferentCouplingRepository;
import com.example.efferent_coupling_api.util.GitCloner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class EfferentCouplingService {
    private static final String CLONE_DIR = "cloned-repos/";

    @Autowired
    private EfferentCouplingRepository repository;

    public Map<String, Integer> processGitHubRepo(String repoUrl) throws Exception {
        File clonedRepo = GitCloner.cloneRepo(repoUrl, CLONE_DIR);
        Map<String, Integer> result = JavaParserUtil.computeEfferentCoupling(clonedRepo);

        Map<String, Integer> sanitized = new HashMap<>();
        for (Map.Entry<String, Integer> entry : result.entrySet()) {
            String safeKey = entry.getKey().replace(".", "_");
            sanitized.put(safeKey, entry.getValue());
        }

        // Store to MongoDB
        EfferentCouplingData data = new EfferentCouplingData();
        data.setRepoUrl(repoUrl);
        data.setCouplingData(sanitized);
        data.setTimestamp(Instant.now().toString());

        repository.save(data);

        return result;
    }
}