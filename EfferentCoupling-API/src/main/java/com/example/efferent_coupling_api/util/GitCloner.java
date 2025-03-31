package com.example.efferent_coupling_api.util;

import org.eclipse.jgit.api.Git;

import java.io.File;

public class GitCloner {
    public static File cloneRepo(String repoUrl, String outputDir) throws Exception {
        String repoName = repoUrl.substring(repoUrl.lastIndexOf('/') + 1).replace(".git", "");
        File localPath = new File(outputDir + repoName);

        if (localPath.exists()) ZipExtractor.deleteDirectory(localPath); // reuse your delete logic
        Git.cloneRepository()
           .setURI(repoUrl)
           .setDirectory(localPath)
           .call();

        return localPath;
    }
}
