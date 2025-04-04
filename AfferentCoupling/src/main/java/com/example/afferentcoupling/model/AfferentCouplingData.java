package com.example.afferentcoupling.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "afferent_coupling_data")
public class AfferentCouplingData {
    @Id
    private String id;
    private String repoUrl;
    private Map<String, Integer> couplingData;
    private String timestamp;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRepoUrl() { return repoUrl; }
    public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }

    public Map<String, Integer> getCouplingData() { return couplingData; }
    public void setCouplingData(Map<String, Integer> couplingData) { this.couplingData = couplingData; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
