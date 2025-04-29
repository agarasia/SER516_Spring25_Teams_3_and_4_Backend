// package com.example.efferent_coupling_api.model;

// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.mapping.Document;

// import com.fasterxml.jackson.annotation.JsonProperty;

// import java.util.List;

// @Document(collection = "efferent_coupling_data")
// public class EfferentCouplingData {
//     @Id
//     private String id;
//     private String repoUrl;

//     @JsonProperty("data")
//     private List<ClassScoreModel> couplingData;
//     private String timestamp;

//     public String getId() { return id; }
//     public void setId(String id) { this.id = id; }

//     public String getRepoUrl() { return repoUrl; }
//     public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }

//     public List<ClassScoreModel> getCouplingData() { return couplingData; }
//     public void setCouplingData(List<ClassScoreModel> couplingData) { this.couplingData = couplingData; }

//     public String getTimestamp() { return timestamp; }
//     public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
// }
