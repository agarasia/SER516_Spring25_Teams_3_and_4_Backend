package com.example.efferent_coupling_api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClassScoreModel {

    @JsonProperty("class_name")
    private String className;

    @JsonProperty("score")
    private Integer classScore;

    public ClassScoreModel() {
    }

    public ClassScoreModel(String className, Integer classScore) {
        this.className = className;
        this.classScore = classScore;
    }
    
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
    
    public Integer getClassScore() {
        return classScore;
    }

    public void setClassScore(Integer classScore) {
        this.classScore = classScore;
    }
}
