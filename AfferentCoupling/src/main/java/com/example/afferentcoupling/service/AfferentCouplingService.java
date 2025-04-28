package com.example.afferentcoupling.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utilities.RepoFetcher;

@Service
public class AfferentCouplingService {
    private static final String CLASS_PATTERN = "package\\s+([\\w\\.]+);.*?class\\s+(\\w+)";
    private static final String IMPORT_PATTERN = "import\\s+([\\w\\.]+);";

    public Map<String, Integer> processGitHubRepo(String repoUrl) {
        // return processGitHubRepo(repoUrl, null);
        try {
            // Use RepoFetcher to check if repo already exists
            RepoFetcher.FetchResult fetchResult = RepoFetcher.fetchRepo(repoUrl);

            if (fetchResult.error != null) {
                throw new RuntimeException("Repo not found: " + fetchResult.error);
            }

            File repoDirectory = new File(fetchResult.repoDir);

            List<String> javaFiles = new ArrayList<>();
            Files.walk(repoDirectory.toPath())
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> {
                        try {
                            javaFiles.add(Files.readString(path));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            return computeCoupling(javaFiles);
        } catch (Exception e) {
            throw new RuntimeException("Error processing GitHub repo: " + e.getMessage());
        }
    }

    public Map<String, Integer> processGitHubRepo(String repoUrl, String token) {
        File tempDir;
        try {
            tempDir = Files.createTempDirectory("repo").toFile();
            
            CloneCommand cloneCommand = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(tempDir);
            
            // Authentication if token is provided
            if (token != null && !token.isEmpty()) {
                cloneCommand.setCredentialsProvider(
                    new UsernamePasswordCredentialsProvider(token, "")
                );
            }
            cloneCommand.call();
            List<String> javaFiles = new ArrayList<>();
            Files.walk(tempDir.toPath())
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> {
                        try {
                            javaFiles.add(Files.readString(path));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            return computeCoupling(javaFiles);
        } catch (Exception e) {
            throw new RuntimeException("Error processing GitHub repo: " + e.getMessage());
        }
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
}