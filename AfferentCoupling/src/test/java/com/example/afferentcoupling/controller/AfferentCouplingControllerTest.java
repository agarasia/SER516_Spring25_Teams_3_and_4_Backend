package com.example.afferentcoupling.controller;

import com.example.afferentcoupling.model.AfferentCouplingData;
import com.example.afferentcoupling.model.CouplingData;
import com.example.afferentcoupling.model.ResponseObject;
import com.example.afferentcoupling.service.AfferentCouplingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AfferentCouplingControllerTest {

    @Mock
    private AfferentCouplingService service;

    @InjectMocks
    private AfferentCouplingController controller;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCoupling_Success() {
        String repoUrl = "https://github.com/ronmamo/reflections";
        AfferentCouplingData mockData = new AfferentCouplingData();
        mockData.setRepoUrl(repoUrl);

        // Mock the service to return a list of AfferentCouplingData
        when(service.getCouplingData(repoUrl)).thenReturn(List.of(mockData));

        // Call the controller method
        ResponseEntity<List<AfferentCouplingData>> response = controller.getCoupling(repoUrl);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(mockData, response.getBody().get(0));

        // Verify the service interaction
        verify(service, times(1)).getCouplingData(repoUrl);
    }

    @Test
    public void testGetCoupling_NotFound() {
        String repoUrl = "https://github.com/ronmamo/reflections";

        // Mock the service to return an empty list
        when(service.getCouplingData(repoUrl)).thenReturn(new ArrayList<>());

        // Call the controller method
        ResponseEntity<List<AfferentCouplingData>> response = controller.getCoupling(repoUrl);

        // Verify the response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        // Verify the service interaction
        verify(service, times(1)).getCouplingData(repoUrl);
    }

    @Test
    public void testComputeFromGitHub_NoContent() {
        String repoUrl = "https://github.com/user/repo";

        // Mock the service to return an empty map
        when(service.processGitHubRepo(repoUrl, null)).thenReturn(new HashMap<>());

        // Call the controller method
        ResponseEntity<ResponseObject> response = controller.computeFromGitHub(repoUrl, null);

        // Verify the response
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        // Verify the service interaction
        verify(service, times(1)).processGitHubRepo(repoUrl, null);
        verify(service, never()).saveCouplingData(anyString(), anyMap());
    }

    @Test
    public void testComputeFromGitHub_Success() {
        String repoUrl = "https://github.com/user/repo";

        // Mock the service to return a valid result
        Map<String, Integer> mockResult = new HashMap<>();
        mockResult.put("com.example.Class1", 3);
        mockResult.put("com.example.Class2", 1);

        when(service.processGitHubRepo(repoUrl, null)).thenReturn(mockResult);

        // Call the controller method
        ResponseEntity<ResponseObject> response = controller.computeFromGitHub(repoUrl, null);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        // Verify the service interaction
        verify(service, times(1)).processGitHubRepo(repoUrl, null);
        verify(service, times(1)).saveCouplingData(eq(repoUrl), eq(mockResult));
    }
}