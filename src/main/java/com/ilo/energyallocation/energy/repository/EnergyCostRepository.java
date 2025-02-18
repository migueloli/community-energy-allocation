package com.ilo.energyallocation.energy.repository;

import com.ilo.energyallocation.energy.model.EnergyCost;
import com.ilo.energyallocation.energy.model.EnergyType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnergyCostRepository extends MongoRepository<EnergyCost, String> {
    Optional<EnergyCost> findByType(EnergyType type);
}
