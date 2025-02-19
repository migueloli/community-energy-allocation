package com.ilo.energyallocation.billing.strategy;

import com.ilo.energyallocation.billing.strategy.interfaces.BillingStrategy;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.EnergyCostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GridBillingStrategy implements BillingStrategy {
    private final EnergyCostRepository costRepository;

    @Override
    public double calculateCost(double consumedEnergy, EnergyType sourceType) {
        return costRepository.findByType(EnergyType.GRID)
                .map(cost -> cost.getCost() * consumedEnergy)
                .orElse(0.0);
    }
}