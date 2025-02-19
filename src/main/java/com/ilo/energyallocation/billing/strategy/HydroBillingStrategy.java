package com.ilo.energyallocation.billing.strategy;

import com.ilo.energyallocation.billing.strategy.interfaces.RenewableBillingStrategy;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.EnergyCostRepository;
import org.springframework.stereotype.Component;

@Component
public class HydroBillingStrategy extends RenewableBillingStrategy {
    public HydroBillingStrategy(EnergyCostRepository costRepository) {
        super(costRepository);
    }

    @Override
    public double calculateCost(double consumedEnergy, EnergyType sourceType) {
        return getCurrentCost() * consumedEnergy;
    }

    @Override
    public EnergyType getEnergyType() {
        return EnergyType.HYDRO;
    }
}
