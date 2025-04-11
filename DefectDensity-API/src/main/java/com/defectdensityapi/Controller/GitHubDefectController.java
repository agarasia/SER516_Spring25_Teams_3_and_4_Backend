package com.defectdensityapi.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.client.RestTemplate;

import com.defectdensityapi.model.DefectDensityHistory;
import com.defectdensityapi.repository.DefectDensityHistoryRepository;
import com.defectdensityapi.util.GithubLinkOwnerRepoExtractor;
import com.defectdensityapi.util.LocApiAdapter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/defects")
public class GitHubDefectController {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final LocApiAdapter locApiAdapter;
    private final DefectDensityHistoryRepository historyRepository;

    public GitHubDefectController(LocApiAdapter locApiAdapter,
                                  DefectDensityHistoryRepository historyRepository) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.locApiAdapter = locApiAdapter;
        this.historyRepository = historyRepository;
    }

    @GetMapping("/repo")
    public ResponseEntity<Map<String, Object>> getDefectRepoCount(
            @RequestParam(value = "url") String url1) throws Exception {

        Map<String, Object> responseMap = new HashMap<>();
        try {
            // Extract "owner/repo" from the GitHub URL
            String repoUrl = GithubLinkOwnerRepoExtractor.extractOwnerRepo(url1);
            if (repoUrl == null) {
                responseMap.put("error", "Invalid GitHub repository URL format.");
                return ResponseEntity.badRequest().body(responseMap);
            }

            // Validate that the URL belongs to GitHub
            if (!url1.startsWith("https://github.com/")) {
                responseMap.put("error", "Provided URL is not a valid GitHub repository.");
                return ResponseEntity.badRequest().body(responseMap);
            }

            // Fetch repo info from GitHub
            String apiUrl = "https://api.github.com/repos/" + repoUrl;
            ResponseEntity<String> githubResponse = restTemplate.getForEntity(apiUrl, String.class);
            JsonNode rootNode = objectMapper.readTree(githubResponse.getBody());
            int openIssuesCount = rootNode.get("open_issues_count").asInt();

            // Fetch total lines of code
            int totalLinesOfCode = locApiAdapter.getTotalLinesOfCode();

            if (totalLinesOfCode == 0) {
                // If zero, return error-like message, but still keep consistent structure
                responseMap.put("defectDensity", "N/A (Total lines of code is zero)");
                responseMap.put("history", null);
                return ResponseEntity.ok(responseMap);
            }

            // Calculate defect density per 1000 lines of code
            double defectDensityPerKLOC = (openIssuesCount * 1000.0) / totalLinesOfCode;

            // Retrieve the history for this repo (descending order by timestamp)
            List<DefectDensityHistory> historyList = historyRepository.findByRepoUrlOrderByTimestampDesc(repoUrl);

            // --- Build the payload in the new desired structure ---
            // Capture the current timestamp and use it for both the current defect and new DB entry
            LocalDateTime currentTimestamp = LocalDateTime.now();

            // Create the "current_defect_density" payload with formatted value (rounded to 2 decimals)
            double defectDensityFormatted = Double.parseDouble(String.format("%.2f", defectDensityPerKLOC));
            Map<String, Object> currentDefectMap = new HashMap<>();
            currentDefectMap.put("timestamp", currentTimestamp.toString());
            Map<String, Object> currentDataMap = new HashMap<>();
            currentDataMap.put("defect_density", defectDensityFormatted);
            currentDefectMap.put("data", currentDataMap);

            // Build the "defect_density_history" list by adding the current measurement as first entry
            List<Map<String, Object>> defectDensityHistoryList = new ArrayList<>();

            // Append each previous history entry (from DB) formatted as required
            for (DefectDensityHistory history : historyList) {
                Map<String, Object> historyEntry = new HashMap<>();
                historyEntry.put("timestamp", history.getTimestamp().toString());
                Map<String, Object> historyData = new HashMap<>();
                historyData.put("defect_density", history.getDefectDensity());
                historyEntry.put("data", historyData);
                defectDensityHistoryList.add(historyEntry);
            }

            // Populate the final response map with the new keys
            responseMap.put("current_defect_density", currentDefectMap);
            responseMap.put("defect_density_history", defectDensityHistoryList);

            // --- Now, after payload preparation, save the new history entry in MongoDB ---
            DefectDensityHistory newHistoryEntry = new DefectDensityHistory();
            newHistoryEntry.setRepoUrl(repoUrl);
            newHistoryEntry.setDefectDensity(defectDensityPerKLOC);
            newHistoryEntry.setTimestamp(currentTimestamp);
            historyRepository.save(newHistoryEntry);

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
