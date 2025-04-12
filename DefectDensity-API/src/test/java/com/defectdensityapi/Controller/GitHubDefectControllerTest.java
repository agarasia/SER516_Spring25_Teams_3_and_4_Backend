package com.defectdensityapi.Controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class GitHubDefectControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testGetDefectRepoCountInvalidUrl() throws Exception {
        mockMvc.perform(get("/api/defects/repo")
                .param("url", "https://notgithub.com/owner/repo")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Provided URL is not a valid GitHub repository."));
    }
    @Test
    public void testMockLocApi() throws Exception {
        mockMvc.perform(get("/api/defects/loc-mock")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLinesOfCode").exists());
    }
}
