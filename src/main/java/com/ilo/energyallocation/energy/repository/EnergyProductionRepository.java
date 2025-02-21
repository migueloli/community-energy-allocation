package com.ilo.energyallocation.energy.repository;

import com.ilo.energyallocation.energy.model.EnergyProduction;
import com.ilo.energyallocation.energy.model.EnergyType;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EnergyProductionRepository extends MongoRepository<EnergyProduction, String> {
    List<EnergyProduction> findByUserIdOrderByTimestampDesc(String userId);

    @Aggregation(
            pipeline = {
                    "{ $match: { timestamp: ?0 } }",
                    "{ $group: { _id: null, total: { $sum: '$amount' } } }"
            }
    )
    double sumProductionByTimestamp(LocalDateTime timestamp);

    @Aggregation(
            pipeline = {
                    "{ $match: { type: ?0, timestamp: ?1 } }",
                    "{ $group: { _id: null, total: { $sum: '$amount' } } }"
            }
    )
    double sumProductionByTypeAndTimestamp(EnergyType type, LocalDateTime timestamp);

    void deleteByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<EnergyProduction> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
}