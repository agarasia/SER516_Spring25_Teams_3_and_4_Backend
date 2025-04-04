package com.example.efferent_coupling_api.util;

import org.eclipse.jgit.api.Git;

import java.io.File;

public class GitCloner {
    public static File cloneRepo(String repoUrl, String outputDir) throws Exception {
        String repoName = repoUrl.substring(repoUrl.lastIndexOf('/') + 1).replace(".git", "");
        File localPath = new File(outputDir, repoName);

        System.out.println("Cloning to: " + localPath.getAbsolutePath());

        // If repo already exists, skip cloning
        if (localPath.exists()) {
            System.out.println("Directory already exists, skipping clone.");
            return localPath;
        }

        Git.cloneRepository()
           .setURI(repoUrl)
           .setDirectory(localPath)
           .call();

        return localPath;
    }
}
