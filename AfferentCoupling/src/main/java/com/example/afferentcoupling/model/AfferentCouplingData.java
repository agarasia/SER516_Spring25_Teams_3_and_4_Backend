package com.example.afferentcoupling.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "afferent_coupling_data")
public class AfferentCouplingData {
    @Id
    private String id;
    private String repoUrl;

    @JsonProperty("data") // Serialize this field as "data" instead of "couplingData"
    private List<CouplingData> couplingData;
    private String timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public List<CouplingData> getCouplingData() {
        return couplingData;
    }

    public void setCouplingData(List<CouplingData> couplingData) {
        this.couplingData = couplingData;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
