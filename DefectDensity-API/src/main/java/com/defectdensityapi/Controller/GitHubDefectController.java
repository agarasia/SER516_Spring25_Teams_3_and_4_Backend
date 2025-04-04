package com.defectdensityapi.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
    
    public GitHubDefectController(LocApiAdapter locApiAdapter) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.locApiAdapter = locApiAdapter;
    }

    @GetMapping("/repo")
    public String getDefectRepoCount(@RequestParam(value="url") String url1) throws Exception {

        try{
            String repoUrl = GithubLinkOwnerRepoExtractor.extractOwnerRepo(url1);
            if (repoUrl == null) {
                return "Error: Invalid GitHub repository URL format."; // More descriptive error message
            }
        
            // Validate that the URL belongs to GitHub
            if (!url1.startsWith("https://github.com/")) {
                return "Error: Provided URL is not a valid GitHub repository.";
            }

            String url = "https://api.github.com/repos/" + repoUrl;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            int openIssuesCount = rootNode.get("open_issues_count").asInt();

            int totalLinesOfCode = locApiAdapter.getTotalLinesOfCode();

            if (totalLinesOfCode == 0) {
                return "Defect Density: N/A (Total lines of code is zero)";
            }
    
            // Calculate defect density per 1000 lines of code
            double defectDensityPerKLOC = (openIssuesCount * 1000.0) / totalLinesOfCode;

            return String.format("%.2f", defectDensityPerKLOC);

        }
        catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            return "Error: Repository not found. Please check the GitHub URL.";
        } 
        catch (org.springframework.web.client.RestClientException e) {
            return "Error: Unable to reach GitHub API. Please try again later.";
        } 
        catch (Exception e) {
            return "Error: An unexpected error occurred - " + e.getMessage();
        }
    }

    @GetMapping("/loc-mock")
    public ResponseEntity<Map<String, Object>> mockLinesOfCodeApi() {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("totalLinesOfCode", locApiAdapter.getTotalLinesOfCode()); 
        return ResponseEntity.ok(mockResponse);
    }
    

}