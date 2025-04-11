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

        when(service.getCouplingData(repoUrl)).thenReturn(List.of(mockData));

        ResponseEntity<List<AfferentCouplingData>> response = controller.getCoupling(repoUrl);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockData, response.getBody());
        verify(service, times(1)).getCouplingData(repoUrl);
    }

    @Test
    public void testComputeFromGitHub_NoContent() {

        String repoUrl = "https://github.com/user/repo";
        when(service.processGitHubRepo(repoUrl, null)).thenReturn(new HashMap<>());

        ResponseEntity<ResponseObject> response = controller.computeFromGitHub(repoUrl, null);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(service, times(1)).processGitHubRepo(repoUrl, null);
        verify(service, never()).saveCouplingData(anyString(), anyMap());
    }
}