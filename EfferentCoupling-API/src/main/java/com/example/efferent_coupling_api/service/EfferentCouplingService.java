package com.example.efferent_coupling_api.service;

import com.example.efferent_coupling_api.util.JavaParserUtil;
import com.example.efferent_coupling_api.util.ZipExtractor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class EfferentCouplingService {
    private static final String CLONE_DIR = "cloned-repos/";

    public Map<String, Integer> processGitHubRepo(String repoUrl) throws Exception {
        File clonedRepo = GitCloner.cloneRepo(repoUrl, CLONE_DIR);
        return JavaParserUtil.computeEfferentCoupling(clonedRepo);
    }
}