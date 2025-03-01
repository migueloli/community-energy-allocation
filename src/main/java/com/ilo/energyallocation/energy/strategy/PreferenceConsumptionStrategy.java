package com.ilo.energyallocation.energy.strategy;

import com.ilo.energyallocation.energy.dto.EnergyConsumptionResponseDTO;
import com.ilo.energyallocation.energy.strategy.interfaces.EnergyConsumptionStrategy;
import com.ilo.energyallocation.user.model.IloUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PreferenceConsumptionStrategy implements EnergyConsumptionStrategy {
    private final SolarEnergyStrategy solarStrategy;
    private final WindEnergyStrategy windStrategy;
    private final HydroEnergyStrategy hydroStrategy;
    private final BiomassEnergyStrategy biomassStrategy;

    @Override
    public EnergyConsumptionResponseDTO consumeEnergy(double requiredAmount, IloUser user, LocalDateTime timeSlot) {
        return switch (user.getPreference()) {
            case SOLAR -> solarStrategy.consumeEnergy(requiredAmount, user, timeSlot);
            case WIND -> windStrategy.consumeEnergy(requiredAmount, user, timeSlot);
            case HYDRO -> hydroStrategy.consumeEnergy(requiredAmount, user, timeSlot);
            case BIOMASS -> biomassStrategy.consumeEnergy(requiredAmount, user, timeSlot);
            default -> null;
        };
    }
}

