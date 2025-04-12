package com.defectdensityapi.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.defectdensityapi.model.DefectDensityHistory;

@Repository
public interface DefectDensityHistoryRepository extends MongoRepository<DefectDensityHistory, String> {
    
    // Example query method to get history for a particular repo, ordered by most recent entry first
    List<DefectDensityHistory> findByRepoUrlOrderByTimestampDesc(String repoUrl);
}
