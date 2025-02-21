package com.ilo.energyallocation.energy.repository;

import com.ilo.energyallocation.energy.model.EnergyConsumptionHistory;
import com.ilo.energyallocation.energy.model.EnergyType;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnergyConsumptionRepository extends MongoRepository<EnergyConsumptionHistory, String> {
    List<EnergyConsumptionHistory> findByUserIdOrderByTimestampDesc(String userId);

    List<EnergyConsumptionHistory> findByUserIdAndTimestampBetween(
            String userId, LocalDateTime startDate, LocalDateTime endDate);

    List<EnergyConsumptionHistory> findByTimestamp(LocalDateTime timestamp);

    @Aggregation(
            pipeline = {
                    "{ $match: { timestamp: ?0 } }",
                    "{ $group: { _id: null, total: { $sum: '$amount' } } }"
            }
    )
    Optional<Double> sumConsumptionByTimestamp(LocalDateTime timestamp);

    @Aggregation(
            pipeline = {
                    "{ $match: { type: ?0, timestamp: ?1 } }",
                    "{ $group: { _id: null, total: { $sum: '$amount' } } }"
            }
    )
    Optional<Double> sumConsumptionByTypeAndTimestamp(EnergyType type, LocalDateTime timestamp);

    void deleteByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
}
