package com.ilo.energyallocation.energy.repository;

import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.model.RemainingEnergy;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RemainingEnergyRepository extends MongoRepository<RemainingEnergy, String> {
    Optional<RemainingEnergy> findFirstByTimeSlotAndTypeOrderByTimeSlotDesc(LocalDateTime timeSlot, EnergyType type);
}
