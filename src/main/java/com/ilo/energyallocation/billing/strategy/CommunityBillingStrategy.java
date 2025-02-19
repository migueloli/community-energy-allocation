package com.ilo.energyallocation.billing.strategy;

import com.ilo.energyallocation.billing.strategy.interfaces.BillingStrategy;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.EnergyCostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommunityBillingStrategy implements BillingStrategy {
    private static final double COMMUNITY_DISCOUNT = 0.5;
    private final EnergyCostRepository costRepository;

    @Override
    public double calculateCost(double consumedEnergy, EnergyType sourceType) {
        return costRepository.findByType(sourceType)
                .map(cost -> cost.getCost() * consumedEnergy * COMMUNITY_DISCOUNT)
                .orElse(0.0);
    }
}