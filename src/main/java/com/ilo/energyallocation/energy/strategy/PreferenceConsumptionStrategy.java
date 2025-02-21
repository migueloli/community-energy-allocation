package com.ilo.energyallocation.energy.strategy;

import com.ilo.energyallocation.energy.dto.EnergyConsumptionResponseDTO;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.EnergyConsumptionRepository;
import com.ilo.energyallocation.energy.repository.EnergyProductionRepository;
import com.ilo.energyallocation.energy.service.RemainingEnergyTrackingService;
import com.ilo.energyallocation.energy.strategy.interfaces.EnergyConsumptionStrategy;
import com.ilo.energyallocation.user.model.EnergyPreference;
import com.ilo.energyallocation.user.model.IloUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class PreferenceConsumptionStrategy implements EnergyConsumptionStrategy {
    private final SolarEnergyStrategy solarStrategy;
    private final WindEnergyStrategy windStrategy;
    private final HydroEnergyStrategy hydroStrategy;
    private final BiomassEnergyStrategy biomassStrategy;
    private final EnergyProductionRepository productionRepository;
    private final EnergyConsumptionRepository consumptionRepository;
    private final RemainingEnergyTrackingService remainingEnergyService;

    @Override
    public EnergyConsumptionResponseDTO consumeEnergy(double requiredAmount, IloUser user) {
        if (user.getPreference() == EnergyPreference.NO_PREFERENCE) {
            return null;
        }

        LocalDateTime timeSlot = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
                .withMinute((LocalDateTime.now().getMinute() / 15) * 15);

        EnergyType preferredType = getEnergyTypeFromPreference(user.getPreference());

        // Use remaining energy instead of total
        double remainingProduction = remainingEnergyService.getRemainingProduction(timeSlot, preferredType);
        double remainingDemand = remainingEnergyService.getRemainingDemand(timeSlot, preferredType);

        double preferredAllocation = calculatePreferredAllocation(
                requiredAmount, remainingProduction, remainingDemand);

        EnergyConsumptionResponseDTO response = allocatePreferredEnergy(
                preferredAllocation, user.getPreference(), user);

        remainingEnergyService.updateRemainingEnergy(timeSlot, preferredType, preferredAllocation);

        return response;
    }

    private double getTotalProduction(EnergyType type) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeStep = now.truncatedTo(ChronoUnit.MINUTES)
                .withMinute((now.getMinute() / 15) * 15);
        return productionRepository.sumProductionByTypeAndTimestamp(type, timeStep);
    }

    private double getTotalDemand(EnergyType type) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeStep = now.truncatedTo(ChronoUnit.MINUTES)
                .withMinute((now.getMinute() / 15) * 15);
        return consumptionRepository.sumConsumptionByTypeAndTimestamp(type, timeStep);
    }


    private double calculatePreferredAllocation(
            double requiredAmount, double remainingProduction, double remainingDemand) {
        if (remainingDemand <= 0) return 0;
        return requiredAmount * (remainingProduction / remainingDemand);
    }

    private EnergyConsumptionResponseDTO allocatePreferredEnergy(
            double allocation, EnergyPreference preference, IloUser user) {
        return switch (preference) {
            case SOLAR -> solarStrategy.consumeEnergy(allocation, user);
            case WIND -> windStrategy.consumeEnergy(allocation, user);
            case HYDRO -> hydroStrategy.consumeEnergy(allocation, user);
            case BIOMASS -> biomassStrategy.consumeEnergy(allocation, user);
            default -> null;
        };
    }

    private EnergyType getEnergyTypeFromPreference(EnergyPreference preference) {
        return switch (preference) {
            case SOLAR -> EnergyType.SOLAR;
            case WIND -> EnergyType.WIND;
            case HYDRO -> EnergyType.HYDRO;
            case BIOMASS -> EnergyType.BIOMASS;
            default -> null;
        };
    }
}

