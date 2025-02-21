package com.ilo.energyallocation.energy.client.interfaces;

import com.ilo.energyallocation.energy.model.EnergyType;

public interface IEnergyPriceClient {
    double getCurrentPrice(EnergyType type);
}
