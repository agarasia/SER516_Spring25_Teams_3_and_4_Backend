package com.example.efferent_coupling_api.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractor {
    public static File extractZipFile(MultipartFile file, String outputDir) throws IOException {
        File uploadDir = new File(outputDir);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        String fileName = file.getOriginalFilename().replace(".zip", "");
        File extractionDir = new File(outputDir + fileName);

        if (extractionDir.exists()) deleteDirectory(extractionDir);
        extractionDir.mkdirs();

        try (ZipInputStream zipIn = new ZipInputStream(file.getInputStream())) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                File newFile = new File(extractionDir, entry.getName());
                if (entry.isDirectory()) newFile.mkdirs();
                else {
                    newFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zipIn.read(buffer)) > 0) fos.write(buffer, 0, length);
                    }
                }
                zipIn.closeEntry();
            }
        }
        return extractionDir;
    }

    private static void deleteDirectory(File dir) throws IOException {
        if (dir.exists()) Files.walk(dir.toPath()).map(Path::toFile).sorted((o1, o2) -> -o1.compareTo(o2)).forEach(File::delete);
    }
}
