package com.example.afferentcoupling.repository;

import com.example.afferentcoupling.model.AfferentCouplingData;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AfferentCouplingRepository extends MongoRepository<AfferentCouplingData, String> {
    List<AfferentCouplingData> findByRepoUrl(String repoUrl);
}