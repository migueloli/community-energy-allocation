package com.ilo.energyallocation.energy.repository;

import com.ilo.energyallocation.energy.model.CommunityEnergyMetrics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CommunityEnergyMetricsRepository extends MongoRepository<CommunityEnergyMetrics, String> {
    void deleteByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
}
