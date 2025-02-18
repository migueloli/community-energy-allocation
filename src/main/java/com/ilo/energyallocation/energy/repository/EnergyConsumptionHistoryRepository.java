package com.ilo.energyallocation.energy.repository;

import com.ilo.energyallocation.energy.model.EnergyConsumptionHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EnergyConsumptionHistoryRepository extends MongoRepository<EnergyConsumptionHistory, String> {
    List<EnergyConsumptionHistory> findByUserIdOrderByTimestampDesc(String userId);

    List<EnergyConsumptionHistory> findByUserIdAndTimestampBetween(
            String userId, LocalDateTime startDate, LocalDateTime endDate);
}
