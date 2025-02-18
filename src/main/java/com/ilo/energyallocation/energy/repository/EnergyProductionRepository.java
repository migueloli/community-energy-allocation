package com.ilo.energyallocation.energy.repository;

import com.ilo.energyallocation.energy.model.EnergyProduction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EnergyProductionRepository extends MongoRepository<EnergyProduction, String> {
    List<EnergyProduction> findByUserIdOrderByTimestampDesc(String userId);

    List<EnergyProduction> findByUserIdAndTimestampBetween(
            String userId, LocalDateTime startDate,
            LocalDateTime endDate
    );
}
