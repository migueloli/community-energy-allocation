package com.ilo.energyallocation.energy.service.interfaces;

import com.ilo.energyallocation.energy.dto.CommunityEnergyResponseDTO;

import java.util.List;

public interface ICommunityEnergyService {
    CommunityEnergyResponseDTO shareEnergy(String userId, double energyAmount);

    double getAvailableCommunityEnergy();

    List<CommunityEnergyResponseDTO> getUserContributions(String userId);
}
