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
public class BiomassEnergyStrategy extends RenewableEnergyStrategy {
    private static final double BIOMASS_COST = 0.12;

    public BiomassEnergyStrategy(EnergyCostRepository costRepository) {
        super(costRepository);
    }

    @Override
    public double getCurrentCost() {
        return BIOMASS_COST;
    }

    @Override
    public EnergyType getEnergyType() {
        return EnergyType.BIOMASS;
    }

    @Override
    public EnergyConsumptionResponseDTO consumeEnergy(double requiredAmount, IloUser user) {
        EnergyConsumptionResponseDTO result = new EnergyConsumptionResponseDTO();
        result.setStrategyUsed("BiomassStrategy");

        EnergySource source = new EnergySource();
        source.setSource(EnergyType.BIOMASS);
        source.setAmount(requiredAmount);

        result.setEnergyConsumed(requiredAmount);
        result.setSourcesUsed(Collections.singletonList(source));
        result.setTotalCost(requiredAmount * BIOMASS_COST);

        return result;
    }
}
