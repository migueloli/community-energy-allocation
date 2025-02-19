package com.ilo.energyallocation.billing.strategy.interfaces;

import com.ilo.energyallocation.energy.model.EnergyType;

public interface BillingStrategy {
    double calculateCost(double consumedEnergy, EnergyType sourceType);
}
