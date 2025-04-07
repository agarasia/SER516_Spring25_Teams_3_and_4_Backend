package com.example.efferent_coupling_api.repository;

import com.example.efferent_coupling_api.model.EfferentCouplingData;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface EfferentCouplingRepository extends MongoRepository<EfferentCouplingData, String> {
    List<EfferentCouplingData> findByRepoUrl(String repoUrl);
}
