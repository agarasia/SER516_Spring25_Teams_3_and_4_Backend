package com.example.efferent_coupling_api.service;

import com.example.efferent_coupling_api.model.EfferentCouplingData;
import com.example.efferent_coupling_api.repository.EfferentCouplingRepository;
import com.example.efferent_coupling_api.util.GitCloner;
import com.example.efferent_coupling_api.util.JavaParserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EfferentCouplingServiceTest {

    @InjectMocks
    private EfferentCouplingService service;

    @Mock
    private EfferentCouplingRepository repository;

    private final String mockRepoUrl = "https://github.com/example/repo";
    private final File mockClonedDir = new File("mock-dir");

    @BeforeEach
    void setup() throws Exception {
        // Mock the static methods
        MockedStatic<GitCloner> gitClonerMock = mockStatic(GitCloner.class);
        MockedStatic<JavaParserUtil> parserMock = mockStatic(JavaParserUtil.class);

        gitClonerMock.when(() -> GitCloner.cloneRepo(eq(mockRepoUrl), anyString()))
                .thenReturn(mockClonedDir);

        Map<String, Integer> fakeCoupling = new HashMap<>();
        fakeCoupling.put("com.example.ClassA", 2);

        parserMock.when(() -> JavaParserUtil.computeEfferentCoupling(mockClonedDir))
                .thenReturn(fakeCoupling);
    }

    
    @Test
    void testProcessGitHubRepo_returnsExpectedResultAndSavesToDb() throws Exception {
        Map<String, Integer> result = service.processGitHubRepo(mockRepoUrl);

        assertEquals(1, result.size());
        assertEquals(2, result.get("com.example.ClassA"));

        verify(repository, times(1)).save(any(EfferentCouplingData.class));
    }
}
