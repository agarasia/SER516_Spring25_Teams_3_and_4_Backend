package com.example.efferent_coupling_api.controller;

// import com.example.efferent_coupling_api.model.ClassScoreModel;
import com.example.efferent_coupling_api.service.EfferentCouplingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EfferentCouplingControllerTest {

    @Mock
    private EfferentCouplingService couplingService;

    @InjectMocks
    private EfferentCouplingController couplingController;

    @Test
    void testAnalyzeFromGitHub() throws Exception {
        String repoUrl = "https://github.com/example/repo.git";

        // Build a fake response matching the service's expected return type.
        Map<String, Object> fakeResponseData = new HashMap<>();
        // List<ClassScoreModel> classScores = new ArrayList<>();
        // classScores.add(new ClassScoreModel("com.example.MyClass", 3));
        // fakeResponseData.put("classScores", classScores);
        // ResponseEntity<Map<String, Object>> expectedResponse = ResponseEntity.ok(fakeResponseData);

        // Stub the service to return a correct ResponseEntity.
        when(couplingService.processGitHubRepo(repoUrl)).thenReturn(expectedResponse);

        ResponseEntity<Map<String, Object>> result = couplingController.analyzeFromGitHub(repoUrl);
        assertEquals(expectedResponse, result);

        verify(couplingService, times(1)).processGitHubRepo(repoUrl);
    }
}
