package com.ilo.energyallocation.energy.repository;

import com.ilo.energyallocation.energy.model.CommunityEnergy;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityEnergyRepository extends MongoRepository<CommunityEnergy, String> {
    List<CommunityEnergy> findByIsConsumedFalseOrderByTimestampAsc();

    List<CommunityEnergy> findByContributorId(String contributorId);
}
