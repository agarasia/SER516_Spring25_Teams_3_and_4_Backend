package com.example.afferentcoupling.service;

import com.example.afferentcoupling.model.AfferentCouplingData;
import com.example.afferentcoupling.repository.AfferentCouplingRepository;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AfferentCouplingService {
   private static final Logger logger = LoggerFactory.getLogger(AfferentCouplingService.class);

   private static final String CLASS_PATTERN = "package\\s+([\\w\\.]+);.*?class\\s+(\\w+)";
   private static final String IMPORT_PATTERN = "import\\s+([\\w\\.]+);";
   private static final Pattern GITHUB_URL_PATTERN = Pattern.compile("^(https://|git@)([^/]+)[/:]([^/]+)/([^/.]+)(\\.git)?$", Pattern.CASE_INSENSITIVE);

   @Autowired
   private AfferentCouplingRepository repository;

   public Map<String, Integer> processGitHubRepo(String repoUrl, String githubToken) {
        if (!isValidGitHubUrl(repoUrl)) {
            throw new IllegalArgumentException("Invalid GitHub URL: " + repoUrl);
        }

    
        File tempDir = null;
        try {
           tempDir = Files.createTempDirectory("repo").toFile();
           logger.info("Cloning repo {} into {}", repoUrl, tempDir.getAbsolutePath());
           CloneCommand clone = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(tempDir);
            
            if (githubToken != null && !githubToken.isEmpty()) {
                logger.info("Using provided GitHub token for private repo");
                clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubToken, ""));
                }
            clone.call();

           List<String> javaFiles = new ArrayList<>();
           Files.walk(tempDir.toPath())
                   .filter(path -> path.toString().endsWith(".java"))
                   .forEach(path -> {
                       try {
                           javaFiles.add(Files.readString(path));
                       } catch (IOException e) {
                           logger.error("Failed to read file: {}", path, e);
                       }
                   });

           Map<String, Integer> result = computeCoupling(javaFiles);
           logger.info(" Coupling result for {}: {}", repoUrl, result);
           return result;
       } catch (Exception e) {
           logger.error("Error processing GitHub repo: {}", repoUrl, e);
           throw new RuntimeException("Error processing GitHub repo: " + e.getMessage());
       } finally {
        if (tempDir != null && tempDir.exists()) {
            try {
                Files.walk(tempDir.toPath())
                        .sorted(Comparator.reverseOrder())
                        .map(java.nio.file.Path::toFile)
                        .forEach(File::delete);
                logger.info("Cleaned up temporary folder: {}", tempDir.getAbsolutePath());
            } catch (IOException cleanupEx) {
                logger.warn("Failed to delete temp folder: {}", tempDir.getAbsolutePath());
            }
        }
    }

   }

   private boolean isValidGitHubUrl(String url) {
    return GITHUB_URL_PATTERN.matcher(url).matches();
   }

   private Map<String, Integer> computeCoupling(List<String> javaFiles) {
       Map<String, Set<String>> classDependencies = new HashMap<>();
       Map<String, Set<String>> afferentCoupling = new HashMap<>();
       Set<String> projectClasses = new HashSet<>();

       for (String fileContent : javaFiles) {
           String className = extractClassName(fileContent);
           if (className != null) {
               projectClasses.add(className);
           }
       }

       for (String fileContent : javaFiles) {
           String className = extractClassName(fileContent);
           if (className != null) {
               Set<String> dependencies = extractDependencies(fileContent, projectClasses);
               classDependencies.put(className, dependencies);
           }
       }

       for (String className : classDependencies.keySet()) {
           afferentCoupling.putIfAbsent(className, new HashSet<>());
       }
       for (Map.Entry<String, Set<String>> entry : classDependencies.entrySet()) {
           String dependentClass = entry.getKey();
           for (String dependency : entry.getValue()) {
               afferentCoupling.putIfAbsent(dependency, new HashSet<>());
               afferentCoupling.get(dependency).add(dependentClass);
           }
       }

       Map<String, Integer> result = new HashMap<>();
       for (Map.Entry<String, Set<String>> entry : afferentCoupling.entrySet()) {
           result.put(entry.getKey(), entry.getValue().size());
       }
       return result;
   }

   private String extractClassName(String content) {
       Pattern pattern = Pattern.compile(CLASS_PATTERN, Pattern.DOTALL);
       Matcher matcher = pattern.matcher(content);
       return matcher.find() ? matcher.group(1) + "." + matcher.group(2) : null;
   }

   private Set<String> extractDependencies(String content, Set<String> projectClasses) {
       Set<String> dependencies = new HashSet<>();
       Pattern pattern = Pattern.compile(IMPORT_PATTERN);
       Matcher matcher = pattern.matcher(content);

       while (matcher.find()) {
           String importedClass = matcher.group(1);
           if (projectClasses.contains(importedClass)) {
               dependencies.add(importedClass);
           }
       }
       return dependencies;
   }

   public void saveCouplingData(String repoUrl, Map<String, Integer> data) {
       logger.info("Saving to Mongo for {} with {} entries", repoUrl, data.size());

       Map<String, Integer> sanitized = new HashMap<>();
       data.forEach((k, v) -> sanitized.put(k.replace(".", "_"), v));

       AfferentCouplingData doc = new AfferentCouplingData();
       doc.setRepoUrl(repoUrl);
       doc.setCouplingData(sanitized);
       doc.setTimestamp(Instant.now().toString());
       repository.save(doc);
   }

   public AfferentCouplingData getCouplingData(String repoUrl) {
       return repository.findByRepoUrl(repoUrl);
   }
}