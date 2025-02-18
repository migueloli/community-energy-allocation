package com.ilo.energyallocation.energy.service.interfaces;

import com.ilo.energyallocation.energy.dto.EnergyConsumptionResponseDTO;
import com.ilo.energyallocation.user.model.IloUser;

public interface IEnergyConsumptionService {
    EnergyConsumptionResponseDTO consumeEnergy(double requiredAmount, IloUser user);
}
