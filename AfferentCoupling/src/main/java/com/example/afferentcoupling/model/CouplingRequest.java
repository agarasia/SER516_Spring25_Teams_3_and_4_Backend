package com.example.afferentcoupling.model;

import java.util.Map;

public class CouplingRequest {
    private String repoUrl;
    private Map<String, Integer> couplingData;

    public String getRepoUrl() { return repoUrl; }
    public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }

    public Map<String, Integer> getCouplingData() { return couplingData; }
    public void setCouplingData(Map<String, Integer> couplingData) { this.couplingData = couplingData; }
}