package com.defectdensityapi.model;

import java.time.LocalDateTime;

public class DefectDensityHistory {

    private String repoUrl;             // owner/repo
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
