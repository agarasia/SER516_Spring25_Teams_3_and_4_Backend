// package com.example.efferent_coupling_api.service;

// // import com.example.efferent_coupling_api.model.ClassScoreModel;
// // import com.example.efferent_coupling_api.model.EfferentCouplingData;
// // import com.example.efferent_coupling_api.repository.EfferentCouplingRepository;
// import com.example.efferent_coupling_api.util.GitCloner;
// import com.example.efferent_coupling_api.util.JavaParserUtil;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockedStatic;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.http.ResponseEntity;

// import java.io.File;
// import java.lang.reflect.Field;
// import java.util.Collections;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// public class EfferentCouplingServiceTest {

//     @InjectMocks
//     private EfferentCouplingService service;

//     // @Mock
//     // private EfferentCouplingRepository repository;

//     private MockedStatic<GitCloner> gitClonerMock;
//     private MockedStatic<JavaParserUtil> javaParserUtilMock;

//     private final String repoUrl = "https://github.com/example/repo.git";
//     private final File dummyDir = new File("dummy-clone");

//     @BeforeEach
//     public void setUp() throws Exception {
//         // Set up static mocks for the utility classes
//         gitClonerMock = mockStatic(GitCloner.class);
//         javaParserUtilMock = mockStatic(JavaParserUtil.class);

//         // Stub GitCloner.cloneRepo to return the dummy directory
//         gitClonerMock.when(() -> GitCloner.cloneRepo(eq(repoUrl), anyString()))
//                      .thenReturn(dummyDir);
        
//         // Stub JavaParserUtil.computeEfferentCoupling to return a test coupling map
//         Map<String, Integer> fakeCoupling = new HashMap<>();
//         fakeCoupling.put("com.example.TestClass", 5);
//         javaParserUtilMock.when(() -> JavaParserUtil.computeEfferentCoupling(dummyDir))
//                           .thenReturn(fakeCoupling);

//         // Since your service injects the repository both via constructor (historyRepository) and via @Autowired (repository),
//         // ensure that the autowired field is properly set:
//         // Field repositoryField = EfferentCouplingService.class.getDeclaredField("repository");
//         // repositoryField.setAccessible(true);
//         // repositoryField.set(service, repository);
//     }

//     @AfterEach
//     public void tearDown() {
//         if (gitClonerMock != null) {
//             gitClonerMock.close();
//         }
//         if (javaParserUtilMock != null) {
//             javaParserUtilMock.close();
//         }
//     }

//     @Test
//     public void testProcessGitHubRepo_returnsExpectedResultAndSavesData() throws Exception {
//         // Stub repository.findByRepoUrl to return an empty history list.
//         // when(repository.findByRepoUrl(repoUrl)).thenReturn(Collections.emptyList());

//         // Execute the service method.
//         ResponseEntity<Map<String, Object>> response = service.processGitHubRepo(repoUrl);

//         // Verify the response is non-null.
//         assertNotNull(response, "Response should not be null");
//         Map<String, Object> body = response.getBody();
//         assertNotNull(body, "Response body should not be null");

//         // Check that "efferent_history" exists and is an empty list.
//         // assertTrue(body.containsKey("efferent_history"), "Response should contain 'efferent_history'");
//         // @SuppressWarnings("unchecked")
//         // List<EfferentCouplingData> historyList = (List<EfferentCouplingData>) body.get("efferent_history");
//         // assertTrue(historyList.isEmpty(), "'efferent_history' should be empty");

//         // Check that "current_efferent" exists with the expected data.
//         assertTrue(body.containsKey("current_efferent"), "Response should contain 'current_efferent'");
//         @SuppressWarnings("unchecked")
//         Map<String, Object> currentEfferent = (Map<String, Object>) body.get("current_efferent");
//         assertTrue(currentEfferent.containsKey("data"), "'current_efferent' should contain 'data'");
//         // @SuppressWarnings("unchecked")
//         // List<ClassScoreModel> currentData = (List<ClassScoreModel>) currentEfferent.get("data");
//         // assertNotNull(currentData, "'data' should not be null");
//         // assertEquals(1, currentData.size(), "There should be one ClassScoreModel entry");

//         // ClassScoreModel model = currentData.get(0);
//         // assertEquals("com.example.TestClass", model.getClassName(), "ClassName should match the expected value");
//         // assertEquals(5, model.getClassScore().intValue(), "ClassScore should be 5");

//         // // Verify that repository.save() was called once.
//         // verify(repository, times(1)).save(any(EfferentCouplingData.class));
//     }
// }
