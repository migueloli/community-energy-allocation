package com.ilo.energyallocation.energy.strategy.interfaces;

import com.ilo.energyallocation.common.exception.ValidationException;
import com.ilo.energyallocation.energy.model.EnergyCost;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.EnergyCostRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class RenewableEnergyStrategy implements EnergyConsumptionStrategy {
    private final EnergyCostRepository costRepository;

    public double getCurrentCost() {
        return costRepository.findByType(getEnergyType()).map(EnergyCost::getCost).orElseThrow(
                () -> new ValidationException("Cost not found for energy type: " + getEnergyType()));
    }

    public abstract EnergyType getEnergyType();
}
