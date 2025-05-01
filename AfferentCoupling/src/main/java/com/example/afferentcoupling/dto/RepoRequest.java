package com.example.afferentcoupling.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RepoRequest {
    
    @JsonProperty("repo_url")
    private String repo_url;

    public String getRepoUrl() {
        return repo_url;
    }

    public void setRepoUrl(String repo_url) {
        this.repo_url = repo_url;
    }
}
