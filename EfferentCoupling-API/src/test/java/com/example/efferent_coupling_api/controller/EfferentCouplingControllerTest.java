package com.example.efferent_coupling_api.controller;

import com.example.efferent_coupling_api.service.EfferentCouplingService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
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

        Map<String, Integer> mockResponse = Collections.singletonMap("com.example.MyClass", 3);
        when(couplingService.processGitHubRepo(repoUrl)).thenReturn(mockResponse);

        Map<String, Integer> result = couplingController.analyzeFromGitHub(repoUrl);

        assertEquals(mockResponse, result);
        verify(couplingService, times(1)).processGitHubRepo(repoUrl);
    }
}
