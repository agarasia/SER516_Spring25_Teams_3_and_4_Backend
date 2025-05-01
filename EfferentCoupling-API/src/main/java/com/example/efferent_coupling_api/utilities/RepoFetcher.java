package com.example.efferent_coupling_api.utilities;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
public class RepoFetcher {
    private static final String SHARED_BASE_DIR = "/shared/repos";
    public static class FetchResult {
        public String headSha;
        public String repoDir;
        public String error;
        public FetchResult(String headSha, String repoDir) {
            this.headSha = headSha;
            this.repoDir = repoDir;
        }
        public FetchResult(String error) {
            this.error = error;
        }
    }
    public static FetchResult fetchRepo(String repoUrl) {
        if (repoUrl == null || repoUrl.isEmpty()) {
            return new FetchResult("No repository URL provided. Please enter a valid GitHub repository URL.");
        }
        URL parsedUrl;
        try {
            parsedUrl = new URL(repoUrl);
        } catch (MalformedURLException e) {
            return new FetchResult("Invalid URL format: " + e.getMessage());
        }
        if (!parsedUrl.getHost().equalsIgnoreCase("github.com") || parsedUrl.getPath().split("/").length < 3) {
            return new FetchResult("Invalid GitHub repository URL. Ensure it follows the format 'https://github.com/owner/repo'.");
        }
        String repoPath = parsedUrl.getPath().replaceFirst("^/", ""); // remove leading '/'
        if (repoPath.endsWith(".git")) {
            repoPath = repoPath.substring(0, repoPath.length() - 4);
        }
        if (!Pattern.matches("^[a-zA-Z0-9_.-]+/[a-zA-Z0-9_.-]+$", repoPath)) {
            return new FetchResult("Malformed repository URL. Ensure the URL points to a valid GitHub repository.");
        }
        String[] parts = repoPath.split("/");
        String owner = parts[0];
        String repo = parts[1];
        Path repoDirPath = Paths.get(SHARED_BASE_DIR, owner, repo);
        File repoDir = repoDirPath.toFile();
        if (!repoDir.exists()) {
            return new FetchResult("Clone the repo first.");
        }
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = builder.setGitDir(new File(repoDir, ".git"))
                    .readEnvironment()
                    .findGitDir()
                    .build();
            String headSha = repository.resolve("HEAD").getName();
            repository.close();
            return new FetchResult(headSha, repoDir.getAbsolutePath());
        } catch (IOException e) {
            return new FetchResult("Error accessing repository: " + e.getMessage());
        }
    }
    // Example usage
    public static void main(String[] args) {
        FetchResult result = fetchRepo("https://github.com/owner/repo");
        if (result.error != null) {
            System.out.println("Error: " + result.error);
        } else {
            System.out.println("Head SHA: " + result.headSha);
            System.out.println("Repo Directory: " + result.repoDir);
        }
    }
}