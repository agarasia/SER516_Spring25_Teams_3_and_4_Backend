package com.defectdensityapi.Controller;

import com.defectdensityapi.util.GithubLinkOwnerRepoExtractor;
import com.defectdensityapi.util.LocApiAdapter;
import com.defectdensityapi.dto.RepoRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/defectdensity")
public class GitHubDefectController {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final LocApiAdapter locApiAdapter;

    public GitHubDefectController(LocApiAdapter locApiAdapter) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.locApiAdapter = locApiAdapter;
    }

    @PostMapping()
    public ResponseEntity<Map<String, Object>> getDefectRepoCount(@RequestBody RepoRequest request) throws Exception {
        Map<String, Object> responseMap = new HashMap<>();
        String url1 = request.getRepoUrl();

        try {
            if (url1 == null || !url1.matches("^https://github\\.com/[\\w.-]+/[\\w.-]+(\\.git)?/?$")) {
                responseMap.put("error", "Provided URL is not a valid GitHub repository.");
                return ResponseEntity.badRequest().body(responseMap);
            }

            // Extract "owner/repo" from GitHub URL
            String repoUrl = GithubLinkOwnerRepoExtractor.extractOwnerRepo(url1);
            if (repoUrl == null) {
                responseMap.put("error", "Invalid GitHub repository URL format.");
                return ResponseEntity.badRequest().body(responseMap);
            }

            // Fetch repo info from GitHub
            String apiUrl = "https://api.github.com/repos/" + repoUrl;
            ResponseEntity<String> githubResponse = restTemplate.getForEntity(apiUrl, String.class);
            JsonNode rootNode = objectMapper.readTree(githubResponse.getBody());
            int openIssuesCount = rootNode.get("open_issues_count").asInt();

            // Get LOC from adapter
            int totalLinesOfCode = locApiAdapter.getTotalLinesOfCode();

            if (totalLinesOfCode == 0) {
                responseMap.put("defectDensity", "N/A (Total lines of code is zero)");
                responseMap.put("history", null);
                return ResponseEntity.ok(responseMap);
            }

            // Calculate defect density per 1000 LOC
            double defectDensityPerKLOC = (openIssuesCount * 1000.0) / totalLinesOfCode;
            double defectDensityFormatted = Double.parseDouble(String.format("%.2f", defectDensityPerKLOC));

            // Timestamp
            LocalDateTime currentTimestamp = LocalDateTime.now();

            // Build response
            Map<String, Object> currentDefectMap = new HashMap<>();
            currentDefectMap.put("timestamp", currentTimestamp.toString());

            Map<String, Object> currentDataMap = new HashMap<>();
            currentDataMap.put("defect_density", defectDensityFormatted);

            currentDefectMap.put("data", currentDataMap);
            responseMap.put("current_defect_density", currentDefectMap);

            return ResponseEntity.ok(responseMap);

        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            responseMap.put("error", "Repository not found. Please check the GitHub URL.");
            return ResponseEntity.badRequest().body(responseMap);
        } catch (org.springframework.web.client.RestClientException e) {
            responseMap.put("error", "Unable to reach GitHub API. Please try again later.");
            return ResponseEntity.status(503).body(responseMap);
        } catch (Exception e) {
            responseMap.put("error", "An unexpected error occurred - " + e.getMessage());
            return ResponseEntity.status(500).body(responseMap);
        }
    }

    @GetMapping("/loc-mock")
    public ResponseEntity<Map<String, Object>> mockLinesOfCodeApi() {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("totalLinesOfCode", locApiAdapter.getTotalLinesOfCode());
        return ResponseEntity.ok(mockResponse);
    }
}
