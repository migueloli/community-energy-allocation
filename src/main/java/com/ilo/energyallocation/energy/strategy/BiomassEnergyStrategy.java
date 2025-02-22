package com.ilo.energyallocation.energy.strategy;

import com.ilo.energyallocation.energy.dto.EnergyConsumptionResponseDTO;
import com.ilo.energyallocation.energy.model.EnergySource;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.EnergyCostRepository;
import com.ilo.energyallocation.energy.service.RemainingEnergyTrackingService;
import com.ilo.energyallocation.energy.strategy.interfaces.DynamicEnergyConsumptionStrategy;
import com.ilo.energyallocation.user.model.IloUser;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BiomassEnergyStrategy extends DynamicEnergyConsumptionStrategy {
    private final RemainingEnergyTrackingService remainingEnergyService;

    public BiomassEnergyStrategy(
            EnergyCostRepository costRepository,
            RemainingEnergyTrackingService remainingEnergyService
    ) {
        super(costRepository);
        this.remainingEnergyService = remainingEnergyService;
    }

    @Override
    public EnergyType getEnergyType() {
        return EnergyType.BIOMASS;
    }

    @Override
    public EnergyConsumptionResponseDTO consumeEnergy(double requiredAmount, IloUser user, LocalDateTime timeSlot) {
        double remainingProduction = remainingEnergyService.getRemainingProduction(timeSlot, EnergyType.BIOMASS);
        double allocatedAmount = Math.min(requiredAmount, remainingProduction);

        EnergyConsumptionResponseDTO result = new EnergyConsumptionResponseDTO();

        EnergySource source = new EnergySource();
        source.setSource(EnergyType.BIOMASS);
        source.setAmount(allocatedAmount);

        result.setEnergyConsumed(allocatedAmount);
        result.addEnergySource(source);
        result.setTotalCost(allocatedAmount * getCurrentCost());

        remainingEnergyService.subtractFromRemainingEnergy(timeSlot, EnergyType.BIOMASS, allocatedAmount);

        return result;
    }
}
