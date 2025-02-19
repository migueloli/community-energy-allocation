package com.ilo.energyallocation.billing.strategy;

import com.ilo.energyallocation.billing.strategy.interfaces.BillingStrategy;
import com.ilo.energyallocation.energy.model.EnergyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SelfProducedBillingStrategy implements BillingStrategy {
    @Override
    public double calculateCost(double consumedEnergy, EnergyType sourceType) {
        return 0.0;
    }
}
