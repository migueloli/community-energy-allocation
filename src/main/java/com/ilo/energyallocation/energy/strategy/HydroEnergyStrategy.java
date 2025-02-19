package com.ilo.energyallocation.energy.strategy;

import com.ilo.energyallocation.energy.dto.EnergyConsumptionResponseDTO;
import com.ilo.energyallocation.energy.model.EnergySource;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.EnergyCostRepository;
import com.ilo.energyallocation.energy.strategy.interfaces.DynamicEnergyConsumptionStrategy;
import com.ilo.energyallocation.user.model.IloUser;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class HydroEnergyStrategy extends DynamicEnergyConsumptionStrategy {
    public HydroEnergyStrategy(EnergyCostRepository costRepository) {
        super(costRepository);
    }

    @Override
    public EnergyType getEnergyType() {
        return EnergyType.HYDRO;
    }

    @Override
    public EnergyConsumptionResponseDTO consumeEnergy(double requiredAmount, IloUser user) {
        EnergyConsumptionResponseDTO result = new EnergyConsumptionResponseDTO();
        result.setStrategyUsed("HydroStrategy");

        EnergySource source = new EnergySource();
        source.setSource(EnergyType.HYDRO);
        source.setAmount(requiredAmount);

        result.setEnergyConsumed(requiredAmount);
        result.setSourcesUsed(Collections.singletonList(source));
        result.setTotalCost(requiredAmount * getCurrentCost());

        return result;
    }
}
