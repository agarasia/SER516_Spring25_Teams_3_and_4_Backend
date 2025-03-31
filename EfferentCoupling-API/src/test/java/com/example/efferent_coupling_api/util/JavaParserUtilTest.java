package com.example.efferent_coupling_api.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JavaParserUtilTest {

    @TempDir
    Path tempDir;

    @Test
    void testComputeEfferentCoupling() throws IOException {
        // Create a fake Java file
        File javaFile = tempDir.resolve("User.java").toFile();
        try (FileWriter writer = new FileWriter(javaFile)) {
            writer.write("package com.example.model;\n" +
                         "import com.example.service.UserService;\n" +
                         "public class User {}");
        }

        // Create a directory and store the Java file
        File repoDir = tempDir.toFile();

        Map<String, Integer> result = JavaParserUtil.computeEfferentCoupling(repoDir);

        assertEquals(1, result.get("com.example.model.User"));
    }
}
