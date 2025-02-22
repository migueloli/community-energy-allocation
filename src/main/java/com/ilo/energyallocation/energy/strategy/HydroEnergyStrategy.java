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
public class HydroEnergyStrategy extends DynamicEnergyConsumptionStrategy {
    private final RemainingEnergyTrackingService remainingEnergyService;

    public HydroEnergyStrategy(
            EnergyCostRepository costRepository,
            RemainingEnergyTrackingService remainingEnergyService
    ) {
        super(costRepository);
        this.remainingEnergyService = remainingEnergyService;
    }

    @Override
    public EnergyType getEnergyType() {
        return EnergyType.HYDRO;
    }

    @Override
    public EnergyConsumptionResponseDTO consumeEnergy(double requiredAmount, IloUser user, LocalDateTime timeSlot) {
        double remainingProduction = remainingEnergyService.getRemainingProduction(timeSlot, EnergyType.HYDRO);
        double allocatedAmount = Math.min(requiredAmount, remainingProduction);

        EnergyConsumptionResponseDTO result = new EnergyConsumptionResponseDTO();

        EnergySource source = new EnergySource();
        source.setSource(EnergyType.HYDRO);
        source.setAmount(allocatedAmount);

        result.setEnergyConsumed(allocatedAmount);
        result.addEnergySource(source);
        result.setTotalCost(allocatedAmount * getCurrentCost());

        remainingEnergyService.subtractFromRemainingEnergy(timeSlot, EnergyType.HYDRO, allocatedAmount);

        return result;
    }
}
