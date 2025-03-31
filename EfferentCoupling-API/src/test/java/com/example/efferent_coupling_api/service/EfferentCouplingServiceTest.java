package com.example.efferent_coupling_api.service;

import com.example.efferent_coupling_api.util.JavaParserUtil;
import com.example.efferent_coupling_api.util.ZipExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EfferentCouplingServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void testProcessZipFile() throws IOException {

        MockMultipartFile file = new MockMultipartFile("file", "test.zip", "application/zip", new byte[]{});

        // Creating a temporary directory for extracted files
        File extractedDir = Files.createTempDirectory(tempDir, "extracted").toFile();

        try (MockedStatic<ZipExtractor> zipMock = Mockito.mockStatic(ZipExtractor.class);
             MockedStatic<JavaParserUtil> parserMock = Mockito.mockStatic(JavaParserUtil.class)) {

            zipMock.when(() -> ZipExtractor.extractZipFile(file, "uploaded-codebases/")).thenReturn(extractedDir);
            parserMock.when(() -> JavaParserUtil.computeEfferentCoupling(extractedDir))
                      .thenReturn(Collections.singletonMap("com.example.model.User", 2));

            EfferentCouplingService service = new EfferentCouplingService();
            Map<String, Integer> result = service.processZipFile(file);

            assertEquals(2, result.get("com.example.model.User"));
        }
    }
}
