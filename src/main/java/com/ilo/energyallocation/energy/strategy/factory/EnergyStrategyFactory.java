package com.ilo.energyallocation.energy.strategy.factory;

import com.ilo.energyallocation.energy.mapper.EnergyPreferenceMapper;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.strategy.BiomassEnergyStrategy;
import com.ilo.energyallocation.energy.strategy.GridEnergyStrategy;
import com.ilo.energyallocation.energy.strategy.HydroEnergyStrategy;
import com.ilo.energyallocation.energy.strategy.SolarEnergyStrategy;
import com.ilo.energyallocation.energy.strategy.WindEnergyStrategy;
import com.ilo.energyallocation.energy.strategy.interfaces.EnergyConsumptionStrategy;
import com.ilo.energyallocation.user.model.EnergyPreference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnergyStrategyFactory {
    private final SolarEnergyStrategy solarStrategy;
    private final WindEnergyStrategy windStrategy;
    private final HydroEnergyStrategy hydroStrategy;
    private final BiomassEnergyStrategy biomassStrategy;
    private final GridEnergyStrategy gridStrategy;
    private final EnergyPreferenceMapper preferenceMapper;

    public EnergyConsumptionStrategy getStrategy(EnergyPreference preference) {
        EnergyType energyType = preferenceMapper.toEnergyType(preference);

        return switch (energyType) {
            case SOLAR -> solarStrategy;
            case WIND -> windStrategy;
            case HYDRO -> hydroStrategy;
            case BIOMASS -> biomassStrategy;
            default -> gridStrategy;
        };
    }
}
