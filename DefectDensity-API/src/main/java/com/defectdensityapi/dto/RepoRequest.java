package com.defectdensityapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RepoRequest {

    @JsonProperty("repo_url")
    private String repoUrl;

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }
}