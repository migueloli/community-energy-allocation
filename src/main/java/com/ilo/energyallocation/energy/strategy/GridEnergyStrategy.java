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
public class GridEnergyStrategy extends DynamicEnergyConsumptionStrategy {

    private final RemainingEnergyTrackingService remainingEnergyService;

    public GridEnergyStrategy(
            EnergyCostRepository costRepository,
            RemainingEnergyTrackingService remainingEnergyService
    ) {
        super(costRepository);
        this.remainingEnergyService = remainingEnergyService;
    }

    @Override
    public EnergyConsumptionResponseDTO consumeEnergy(double requiredAmount, IloUser user, LocalDateTime timeSlot) {
        EnergyConsumptionResponseDTO result = new EnergyConsumptionResponseDTO();

        EnergySource source = new EnergySource();
        source.setSource(EnergyType.GRID);
        source.setAmount(requiredAmount);

        result.setEnergyConsumed(requiredAmount);
        result.addEnergySource(source);
        result.setTotalCost(requiredAmount * getCurrentCost());

        remainingEnergyService.subtractFromRemainingEnergy(timeSlot, EnergyType.GRID, requiredAmount);

        return result;
    }

    @Override
    public EnergyType getEnergyType() {
        return EnergyType.GRID;
    }
}
