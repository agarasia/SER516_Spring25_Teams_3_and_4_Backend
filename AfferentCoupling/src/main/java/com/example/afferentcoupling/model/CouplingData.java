package com.example.afferentcoupling.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CouplingData {
    @JsonProperty("class_name")
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @JsonProperty("score")
    private Integer couplingScore;

    public Integer getCouplingScore() {
        return couplingScore;
    }

    public void setCouplingScore(Integer couplingScore) {
        this.couplingScore = couplingScore;
    }

    public CouplingData(String className, Integer couplingScore) {
        this.className = className;
        this.couplingScore = couplingScore;
    }

}
