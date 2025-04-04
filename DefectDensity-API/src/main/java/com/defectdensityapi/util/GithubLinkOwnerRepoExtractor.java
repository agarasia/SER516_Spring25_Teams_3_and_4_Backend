package com.defectdensityapi.util;

public class GithubLinkOwnerRepoExtractor {

    public static String extractOwnerRepo(String repo) {

        if (repo == null) {
            return null;
        }
        
        // Remove .git extension if present and GitHub URL prefix.
        repo = repo.replace(".git", "");
        repo = repo.replace("https://github.com/", "");
        
        // Remove any trailing slashes.
        while (repo.endsWith("/")) {
            repo = repo.substring(0, repo.length() - 1);
        }
        
        // Split the remaining string by "/" to extract the owner and repository name.
        String[] parts = repo.split("/");
        
        // Ensure we have at least two segments (owner and repo).
        if (parts.length >= 2) {
            return parts[0] + "/" + parts[1];
        } else {
            return null;
        }
    }
}
