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
import java.time.temporal.ChronoUnit;

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
    public EnergyConsumptionResponseDTO consumeEnergy(double requiredAmount, IloUser user) {
        LocalDateTime timeSlot = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
                .withMinute((LocalDateTime.now().getMinute() / 15) * 15);

        double remainingProduction = remainingEnergyService.getRemainingProduction(timeSlot, EnergyType.GRID);
        double allocatedAmount = Math.min(requiredAmount, remainingProduction);

        EnergyConsumptionResponseDTO result = new EnergyConsumptionResponseDTO();

        EnergySource source = new EnergySource();
        source.setSource(EnergyType.GRID);
        source.setAmount(allocatedAmount);

        result.setEnergyConsumed(allocatedAmount);
        result.addEnergySource(source);
        result.setTotalCost(allocatedAmount * getCurrentCost());

        remainingEnergyService.updateRemainingEnergy(timeSlot, EnergyType.GRID, allocatedAmount);

        return result;
    }

    @Override
    public EnergyType getEnergyType() {
        return EnergyType.GRID;
    }
}
