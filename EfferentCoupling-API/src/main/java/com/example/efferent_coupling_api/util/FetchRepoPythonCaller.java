package com.example.efferent_coupling_api.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FetchRepoPythonCaller {

    public static FetchRepoResult fetchRepo(String repoUrl) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("python3", "main/utilities/fetch_repo.py", repoUrl);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        StringBuilder output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("fetch_repo.py exited with code " + exitCode + ". Output: " + output);
        }

        // Output is expected like:  ('commit_sha', '/shared/repos/owner/repo')
        String result = output.toString().trim();
        if (result.startsWith("(") && result.endsWith(")")) {
            result = result.substring(1, result.length() - 1);
        }

        String[] parts = result.split(",", 2);
        if (parts.length != 2) {
            throw new RuntimeException("Unexpected output from fetch_repo.py: " + output);
        }

        String commitSha = parts[0].trim().replace("'", "");
        String repoPath = parts[1].trim().replace("'", "");

        return new FetchRepoResult(commitSha, repoPath);
    }

    public static class FetchRepoResult {
        public final String commitSha;
        public final String repoPath;

        public FetchRepoResult(String commitSha, String repoPath) {
            this.commitSha = commitSha;
            this.repoPath = repoPath;
        }
    }
}
