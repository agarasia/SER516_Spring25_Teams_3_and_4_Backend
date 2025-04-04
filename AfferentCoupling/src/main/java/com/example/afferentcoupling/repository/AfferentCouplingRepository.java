package com.example.afferentcoupling.repository;

import com.example.afferentcoupling.model.AfferentCouplingData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AfferentCouplingRepository extends MongoRepository<AfferentCouplingData, String> {
    AfferentCouplingData findByRepoUrl(String repoUrl);
}