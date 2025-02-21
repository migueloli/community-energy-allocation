package com.ilo.energyallocation.energy.service;

import com.ilo.energyallocation.energy.model.CommunityEnergyMetrics;
import com.ilo.energyallocation.energy.model.EnergyConsumptionHistory;
import com.ilo.energyallocation.energy.model.EnergyCost;
import com.ilo.energyallocation.energy.model.EnergySource;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.CommunityEnergyMetricsRepository;
import com.ilo.energyallocation.energy.repository.EnergyConsumptionRepository;
import com.ilo.energyallocation.energy.repository.EnergyCostRepository;
import com.ilo.energyallocation.energy.repository.EnergyProductionRepository;
import com.ilo.energyallocation.energy.service.interfaces.IDemandCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DemandCalculationService implements IDemandCalculationService {

    private final EnergyProductionRepository productionRepository;
    private final EnergyConsumptionRepository consumptionRepository;
    private final CommunityEnergyMetricsRepository metricsRepository;
    private final EnergyCostRepository energyCostRepository;

    @Scheduled(fixedRate = 900000) // 15 minutes
    @Override
    public void calculateDemandAllocation() {
        LocalDateTime timeStep = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
                .withMinute((LocalDateTime.now().getMinute() / 15) * 15);

        updateEnergyCosts(timeStep);

        double totalProduction = calculateTotalProduction(timeStep);
        double totalDemand = calculateTotalDemand(timeStep);

        Map<String, List<EnergySource>> energyAllocations = calculateEnergyAllocations(
                totalProduction, totalDemand, timeStep);
        CommunityEnergyMetrics metrics = calculateCommunityMetrics(totalProduction, totalDemand, timeStep);

        saveResults(energyAllocations, metrics, timeStep);
    }

    @Override
    public void updateEnergyCosts(LocalDateTime timeStep) {
        Arrays.stream(EnergyType.values())
                .forEach(type -> {
                    double newCost = calculateNewCost(type, timeStep);
                    EnergyCost cost = energyCostRepository.findByType(type)
                            .orElse(EnergyCost.builder().type(type).build());
                    cost.setCost(newCost);
                    cost.setLastUpdated(timeStep);
                    energyCostRepository.save(cost);
                });
    }

    @Override
    public double calculateNewCost(EnergyType type, LocalDateTime timeStep) {
        double production = productionRepository.sumProductionByTypeAndTimestamp(type, timeStep).orElse(0.0);
        double demand = consumptionRepository.sumConsumptionByTypeAndTimestamp(type, timeStep).orElse(0.0);

        if (demand <= 0) return type.getBasePrice();

        double demandFactor = demand / Math.max(production, 1);
        return type.getBasePrice() * Math.min(demandFactor, 2.0); // Cap at 2x base price
    }

    private void saveResults(
            Map<String, List<EnergySource>> energyAllocations,
            CommunityEnergyMetrics metrics,
            LocalDateTime timeStep
    ) {
        List<EnergyConsumptionHistory> consumptions = consumptionRepository.findByTimestamp(timeStep);
        consumptions.forEach(consumption -> {
            List<EnergySource> sources = energyAllocations.get(consumption.getUserId());
            consumption.setSourcesUsed(sources);
            consumption.setTotalCost(calculateTotalCost(sources));
            consumptionRepository.save(consumption);
        });

        metricsRepository.save(metrics);
    }

    private Map<String, List<EnergySource>> calculateEnergyAllocations(
            double totalProduction, double totalDemand, LocalDateTime timeStep) {
        Map<String, List<EnergySource>> allocations = new HashMap<>();
        List<EnergyConsumptionHistory> consumptions = consumptionRepository.findByTimestamp(timeStep);

        double allocationRatio = totalProduction >= totalDemand ? 1.0 : totalProduction / totalDemand;

        consumptions.forEach(consumption -> {
            List<EnergySource> sources = calculateEnergySourcesForConsumption(
                    consumption.getAmount(), allocationRatio);
            allocations.put(consumption.getUserId(), sources);
        });

        return allocations;
    }

    private List<EnergySource> calculateEnergySourcesForConsumption(double amount, double allocationRatio) {
        List<EnergySource> sources = new ArrayList<>();
        double localEnergy = amount * allocationRatio;
        double gridEnergy = amount - localEnergy;

        if (localEnergy > 0) {
            sources.add(new EnergySource(EnergyType.SOLAR, localEnergy * 0.4));
            sources.add(new EnergySource(EnergyType.WIND, localEnergy * 0.3));
            sources.add(new EnergySource(EnergyType.HYDRO, localEnergy * 0.2));
            sources.add(new EnergySource(EnergyType.BIOMASS, localEnergy * 0.1));
        }

        if (gridEnergy > 0) {
            sources.add(new EnergySource(EnergyType.GRID, gridEnergy));
        }

        return sources;
    }


    private double calculateTotalCost(List<EnergySource> sources) {
        return sources.stream()
                .mapToDouble(source -> source.getAmount() *
                        energyCostRepository.findByType(source.getSource())
                                .map(EnergyCost::getCost)
                                .orElse(source.getSource().getBasePrice()))
                .sum();
    }

    private CommunityEnergyMetrics calculateCommunityMetrics(
            double totalProduction, double totalDemand, LocalDateTime timeStep) {
        return CommunityEnergyMetrics.builder()
                .timestamp(timeStep)
                .totalProduction(totalProduction)
                .totalDemand(totalDemand)
                .totalGridEnergy(Math.max(0, totalDemand - totalProduction))
                .totalSurplus(Math.max(0, totalProduction - totalDemand))
                .build();
    }

    private double calculateTotalProduction(LocalDateTime timeStep) {
        return productionRepository.sumProductionByTimestamp(timeStep).orElse(0.0);
    }

    private double calculateTotalDemand(LocalDateTime timeStep) {
        return consumptionRepository.sumConsumptionByTimestamp(timeStep).orElse(0.0);
    }
}
