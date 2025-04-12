package com.defectdensityapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "defect_density_history")
public class DefectDensityHistory {

    @Id
    private String id;                  // MongoDB document ID
    private String repoUrl;             // e.g., "owner/repo"
    private double defectDensity;       // The calculated defect density
    private LocalDateTime timestamp;    // Time when the density was recorded

    public DefectDensityHistory() {
    }

    public DefectDensityHistory(String repoUrl, double defectDensity, LocalDateTime timestamp) {
        this.repoUrl = repoUrl;
        this.defectDensity = defectDensity;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public double getDefectDensity() {
        return defectDensity;
    }

    public void setDefectDensity(double defectDensity) {
        this.defectDensity = defectDensity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
