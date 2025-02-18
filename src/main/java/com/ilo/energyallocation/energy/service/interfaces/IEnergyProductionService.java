package com.ilo.energyallocation.energy.service.interfaces;

import com.ilo.energyallocation.energy.dto.EnergyProductionRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyProductionResponseDTO;
import com.ilo.energyallocation.user.model.IloUser;

import java.util.List;

public interface IEnergyProductionService {
    EnergyProductionResponseDTO logProduction(
            final IloUser currentUser,
            final EnergyProductionRequestDTO request
    );

    List<EnergyProductionResponseDTO> getProductionHistory(final IloUser currentUser);
}
