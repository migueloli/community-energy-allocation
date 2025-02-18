package com.ilo.energyallocation.energy.service.interfaces;

import com.ilo.energyallocation.energy.dto.EnergyProductionRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyProductionResponseDTO;

import java.util.List;

public interface IEnergyProductionService {
    EnergyProductionResponseDTO logProduction(String userId, EnergyProductionRequestDTO request);

    List<EnergyProductionResponseDTO> getProductionHistory(String userId);
}
