package com.ilo.energyallocation.energy.strategy;

import com.ilo.energyallocation.energy.dto.EnergyConsumptionResponseDTO;
import com.ilo.energyallocation.energy.model.EnergyProduction;
import com.ilo.energyallocation.energy.model.EnergySource;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.EnergyProductionRepository;
import com.ilo.energyallocation.energy.service.RemainingEnergyTrackingService;
import com.ilo.energyallocation.energy.strategy.interfaces.EnergyConsumptionStrategy;
import com.ilo.energyallocation.user.model.IloUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SelfProducedEnergyStrategy implements EnergyConsumptionStrategy {
    private final EnergyProductionRepository productionRepository;
    private final RemainingEnergyTrackingService remainingEnergyService;

    @Override
    public EnergyConsumptionResponseDTO consumeEnergy(double requiredAmount, IloUser user, LocalDateTime timeSlot) {
        List<EnergyProduction> productions = productionRepository.findByUserIdOrderByTimestampDesc(user.getId());

        if (productions.isEmpty()) {
            return null;
        }

        Map<EnergyType, Double> availableEnergy = remainingEnergyService.getRemainingEnergy(timeSlot);
        double totalAvailable = availableEnergy.values().stream().mapToDouble(Double::doubleValue).sum();

        if (totalAvailable <= 0) {
            return null;
        }

        double energyToConsume = Math.min(requiredAmount, totalAvailable);
        EnergyConsumptionResponseDTO result = new EnergyConsumptionResponseDTO();
        result.setEnergyConsumed(energyToConsume);
        result.setTotalCost(0.0); // Self-produced energy is free

        List<EnergySource> sources = calculateEnergySourcesUsed(availableEnergy, energyToConsume);
        result.addEnergySourceList(sources);

        // Update remaining energy
        sources.forEach(source ->
                remainingEnergyService.consumeEnergy(source.getSource(), source.getAmount(), timeSlot)
        );

        return result;
    }

    private List<EnergySource> calculateEnergySourcesUsed(Map<EnergyType, Double> available, double requiredAmount) {
        List<EnergySource> sources = new ArrayList<>();
        double remaining = requiredAmount;

        for (Map.Entry<EnergyType, Double> entry : available.entrySet()) {
            if (remaining <= 0) break;
            if (entry.getValue() > 0) {
                double amount = Math.min(entry.getValue(), remaining);
                sources.add(new EnergySource(entry.getKey(), amount));
                remaining -= amount;
            }
        }

        return sources;
    }
}