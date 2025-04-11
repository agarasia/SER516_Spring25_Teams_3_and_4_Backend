package com.example.afferentcoupling.service;

import com.example.afferentcoupling.model.AfferentCouplingData;
import com.example.afferentcoupling.model.CouplingData;
import com.example.afferentcoupling.repository.AfferentCouplingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AfferentCouplingServiceTest {

    @Mock
    private AfferentCouplingRepository repository;

    @InjectMocks
    private AfferentCouplingService service;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCouplingData() {
        String repoUrl = "https://github.com/ronmamo/reflections";

        AfferentCouplingData expectedData = new AfferentCouplingData();
        expectedData.setRepoUrl(repoUrl);

        when(repository.findByRepoUrl(repoUrl)).thenReturn(List.of(expectedData));
        List<AfferentCouplingData> actualData = service.getCouplingData(repoUrl);
        assertEquals(expectedData, actualData);
        verify(repository, times(1)).findByRepoUrl(repoUrl);
    }

    @Test
    public void testSaveCouplingData() {
        String repoUrl = "https://github.com/user/repo";
        Map<String, Integer> couplingData = new HashMap<>();
        couplingData.put("com.example.Class1", 3);
        couplingData.put("com.example.Class2", 1);

        ArgumentCaptor<AfferentCouplingData> dataCaptor = ArgumentCaptor.forClass(AfferentCouplingData.class);

        service.saveCouplingData(repoUrl, couplingData);

        verify(repository, times(1)).save(dataCaptor.capture());
        AfferentCouplingData savedData = dataCaptor.getValue();

        assertEquals(repoUrl, savedData.getRepoUrl());
        assertEquals(2, savedData.getCouplingData().size());

        List<CouplingData> couplingDataList = savedData.getCouplingData();
        assertTrue(couplingDataList.stream()
                .anyMatch(data -> data.getClassName().equals("Class1") && data.getCouplingScore() == 3));
        assertTrue(couplingDataList.stream()
                .anyMatch(data -> data.getClassName().equals("Class2") && data.getCouplingScore() == 1));
        assertNotNull(savedData.getTimestamp());
    }

    @Test
    public void testIsValidGitHubUrl() {
        String validUrl1 = "https://github.com/user/repo";
        String invalidUrl1 = "https://gitlab.com/user/repo";
        String invalidUrl2 = "not a url";

        assertTrue((Boolean) ReflectionTestUtils.invokeMethod(service, "isValidGitHubUrl", validUrl1));
        assertFalse((Boolean) ReflectionTestUtils.invokeMethod(service, "isValidGitHubUrl", invalidUrl1));
        assertFalse((Boolean) ReflectionTestUtils.invokeMethod(service, "isValidGitHubUrl", invalidUrl2));
    }

    @Test
    public void testComputeCoupling() {
        List<String> javaFiles = Arrays.asList(
                "package com.example.test;\n" +
                        "import com.example.test.ClassA;\n" +
                        "public class ClassB {\n" +
                        "    private ClassA classA;\n" +
                        "}\n",

                "package com.example.test;\n" +
                        "import com.example.test.ClassB;\n" +
                        "public class ClassA {\n" +
                        "    private ClassB classB;\n" +
                        "}\n",

                "package com.example.test;\n" +
                        "import com.example.test.ClassA;\n" +
                        "import com.example.test.ClassB;\n" +
                        "public class ClassC {\n" +
                        "    private ClassA classA;\n" +
                        "    private ClassB classB;\n" +
                        "}\n");

        @SuppressWarnings("unchecked")
        Map<String, Integer> result = (Map<String, Integer>) ReflectionTestUtils.invokeMethod(
                service, "computeCoupling", javaFiles);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(2, result.get("ClassA"));
        assertEquals(2, result.get("ClassB"));
        assertEquals(0, result.get("ClassC"));
    }

}