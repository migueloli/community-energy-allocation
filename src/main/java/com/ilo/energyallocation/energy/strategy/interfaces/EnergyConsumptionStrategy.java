package com.ilo.energyallocation.energy.strategy.interfaces;

import com.ilo.energyallocation.energy.dto.EnergyConsumptionResponseDTO;
import com.ilo.energyallocation.user.model.IloUser;

public interface EnergyConsumptionStrategy {
    EnergyConsumptionResponseDTO consumeEnergy(double requiredAmount, IloUser user);
}
