package com.ilo.energyallocation.energy.client;

import com.ilo.energyallocation.energy.client.interfaces.IEnergyPriceClient;
import com.ilo.energyallocation.energy.model.EnergyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalEnergyPriceClient implements IEnergyPriceClient {

    public double getCurrentPrice(EnergyType type) {
        // Simulate external API call with realistic price fluctuations
        double basePrice = type.getBasePrice();
        double fluctuation = 0.2; // 20% max fluctuation
        double randomFactor = 1.0 + (Math.random() * fluctuation * 2 - fluctuation);

        return basePrice * randomFactor;
    }
}
