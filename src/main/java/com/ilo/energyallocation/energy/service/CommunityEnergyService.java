package com.ilo.energyallocation.energy.service;

import com.ilo.energyallocation.energy.dto.CommunityEnergyResponseDTO;
import com.ilo.energyallocation.energy.mapper.CommunityEnergyMapper;
import com.ilo.energyallocation.energy.model.CommunityEnergy;
import com.ilo.energyallocation.energy.repository.CommunityEnergyRepository;
import com.ilo.energyallocation.energy.service.interfaces.ICommunityEnergyService;
import com.ilo.energyallocation.energy.strategy.factory.EnergyStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityEnergyService implements ICommunityEnergyService {
    private final CommunityEnergyRepository communityEnergyRepository;
    private final CommunityEnergyMapper communityEnergyMapper;
    private final EnergyStrategyFactory strategyFactory;

    @Override
    public CommunityEnergyResponseDTO shareEnergy(String userId, double energyAmount) {
        CommunityEnergy sharedEnergy = new CommunityEnergy();
        sharedEnergy.setContributorId(userId);
        sharedEnergy.setAvailableEnergy(energyAmount);
        sharedEnergy.setTimestamp(LocalDateTime.now());
        sharedEnergy.setConsumed(false);

        return communityEnergyMapper.toResponse(communityEnergyRepository.save(sharedEnergy));
    }

    @Override
    public double getAvailableCommunityEnergy() {
        List<CommunityEnergy> availableEnergy =
                communityEnergyRepository.findByIsConsumedFalseOrderByTimestampAsc();

        return availableEnergy.stream().mapToDouble(CommunityEnergy::getAvailableEnergy).sum();
    }

    @Override
    public List<CommunityEnergyResponseDTO> getUserContributions(String userId) {
        List<CommunityEnergy> contributions = communityEnergyRepository.findByContributorId(userId);
        return communityEnergyMapper.toResponseList(contributions);
    }
}