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
    void testAnalyzeZipFile() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "test.zip", "application/zip", new byte[]{});

        // Mocking service response
        Map<String, Integer> mockResponse = Collections.singletonMap("com.example.model.User", 2);
        when(couplingService.processZipFile(file)).thenReturn(mockResponse);

        Map<String, Integer> result = couplingController.analyzeZipFile(file);

        assertEquals(mockResponse, result);
        verify(couplingService, times(1)).processZipFile(file);
    }
}
