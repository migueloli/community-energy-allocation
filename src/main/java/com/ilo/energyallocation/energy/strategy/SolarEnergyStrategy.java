package com.ilo.energyallocation.energy.strategy;

import com.ilo.energyallocation.energy.dto.EnergyConsumptionResponseDTO;
import com.ilo.energyallocation.energy.model.EnergySource;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.EnergyCostRepository;
import com.ilo.energyallocation.energy.strategy.interfaces.RenewableEnergyStrategy;
import com.ilo.energyallocation.user.model.IloUser;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class SolarEnergyStrategy extends RenewableEnergyStrategy {
    private static final double SOLAR_COST = 0.08;

    public SolarEnergyStrategy(EnergyCostRepository costRepository) {
        super(costRepository);
    }

    @Override
    public double getCurrentCost() {
        return SOLAR_COST;
    }

    @Override
    public EnergyType getEnergyType() {
        return EnergyType.SOLAR;
    }

    @Override
    public EnergyConsumptionResponseDTO consumeEnergy(double requiredAmount, IloUser user) {
        EnergyConsumptionResponseDTO result = new EnergyConsumptionResponseDTO();
        result.setStrategyUsed("SolarStrategy");

        EnergySource source = new EnergySource();
        source.setSource(EnergyType.SOLAR);
        source.setAmount(requiredAmount);

        result.setEnergyConsumed(requiredAmount);
        result.setSourcesUsed(Collections.singletonList(source));
        result.setTotalCost(requiredAmount * SOLAR_COST);

        return result;
    }
}
