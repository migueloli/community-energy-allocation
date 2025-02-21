package com.ilo.energyallocation.energy.service;

import com.ilo.energyallocation.energy.model.CommunityEnergyMetrics;
import com.ilo.energyallocation.energy.model.EnergyConsumption;
import com.ilo.energyallocation.energy.model.EnergyConsumptionHistory;
import com.ilo.energyallocation.energy.model.EnergyCost;
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

    @Scheduled(fixedRate = 900000) // 15 minutes in milliseconds
    @Override
    public void calculateDemandAllocation() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeStep = now.truncatedTo(ChronoUnit.MINUTES)
                .withMinute((now.getMinute() / 15) * 15);

        // Update energy costs first
        updateEnergyCosts(timeStep);

        // Existing demand calculation logic
        double totalProduction = calculateTotalProduction(timeStep);
        double totalDemand = calculateTotalDemand(timeStep);


        // Step 2: Calculate local energy allocation
        Map<String, Double> localEnergyAllocations = calculateLocalEnergyAllocations(
                totalProduction, totalDemand, timeStep);

        // Step 3: Calculate grid energy allocations
        Map<String, Double> gridEnergyAllocations = calculateGridEnergyAllocations(
                totalProduction, totalDemand, timeStep);

        // Step 4: Calculate community metrics
        CommunityEnergyMetrics metrics = calculateGridMetrics(
                totalProduction, totalDemand, localEnergyAllocations, timeStep);

        // Store the results
        saveResults(localEnergyAllocations, gridEnergyAllocations, metrics, timeStep);
    }

    @Override
    public void updateEnergyCosts(LocalDateTime timeStep) {
        Arrays.stream(EnergyType.values()).forEach(type -> {
            EnergyCost cost = energyCostRepository.findByType(type)
                    .orElse(EnergyCost.builder().type(type).build());

            // Calculate new cost based on supply and demand
            double newCost = calculateNewCost(type, timeStep);

            cost.setCost(newCost);
            cost.setLastUpdated(timeStep);
            energyCostRepository.save(cost);
        });
    }

    @Override
    public double calculateNewCost(EnergyType type, LocalDateTime timeStep) {
        double production = productionRepository.sumProductionByTypeAndTimestamp(type, timeStep);
        double demand = consumptionRepository.sumConsumptionByTypeAndTimestamp(type, timeStep);

        // Basic cost calculation based on supply and demand ratio
        // This can be enhanced with more sophisticated pricing models
        if (demand <= 0) return type.getBasePrice();
        return type.getBasePrice() * (demand / Math.max(production, 1));
    }

    private Map<String, Double> calculateGridEnergyAllocations(
            double totalProduction, double totalDemand, LocalDateTime timeStep) {
        Map<String, Double> gridAllocations = new HashMap<>();
        List<EnergyConsumption> consumptions = consumptionRepository.findByTimestamp(timeStep);

        if (totalProduction < totalDemand) {
            consumptions.forEach(consumption -> {
                double gridEnergy = consumption.getAmount() -
                        (consumption.getAmount() * totalProduction / totalDemand);
                gridAllocations.put(consumption.getUserId(), gridEnergy);
            });
        } else {
            consumptions.forEach(consumption ->
                    gridAllocations.put(consumption.getUserId(), 0.0));
        }

        return gridAllocations;
    }

    private void saveResults(
            Map<String, Double> localEnergyAllocations,
            Map<String, Double> gridEnergyAllocations,
            CommunityEnergyMetrics metrics,
            LocalDateTime timeStep
    ) {
        // Save individual allocations
        List<EnergyConsumption> consumptions = consumptionRepository.findByTimestamp(timeStep);
        consumptions.forEach(consumption -> {
            EnergyConsumptionHistory history = new EnergyConsumptionHistory();
            history.setUserId(consumption.getUserId());
            history.setTimestamp(timeStep);
            history.setAmount(consumption.getAmount());
            history.setLocalEnergyAllocated(
                    localEnergyAllocations.getOrDefault(consumption.getUserId(), 0.0));
            history.setGridEnergyAllocated(
                    gridEnergyAllocations.getOrDefault(consumption.getUserId(), 0.0));
            consumptionRepository.save(history);
        });

        // Save community metrics
        metricsRepository.save(metrics);
    }

    private double calculateTotalProduction(LocalDateTime timeStep) {
        return productionRepository.sumProductionByTimestamp(timeStep);
    }

    private double calculateTotalDemand(LocalDateTime timeStep) {
        return consumptionRepository.sumConsumptionByTimestamp(timeStep);
    }

    private Map<String, Double> calculateLocalEnergyAllocations(
            double totalProduction, double totalDemand, LocalDateTime timeStep) {

        Map<String, Double> allocations = new HashMap<>();
        List<EnergyConsumption> consumptions = consumptionRepository.findByTimestamp(timeStep);

        if (totalProduction < totalDemand) {
            double ratio = totalProduction / totalDemand;
            consumptions.forEach(consumption -> {
                double allocation = consumption.getAmount() * ratio;
                allocations.put(consumption.getUserId(), allocation);
            });
        } else {
            consumptions.forEach(consumption -> {
                allocations.put(consumption.getUserId(), consumption.getAmount());
            });
        }

        return allocations;
    }

    private CommunityEnergyMetrics calculateGridMetrics(
            double totalProduction, double totalDemand,
            Map<String, Double> localAllocations, LocalDateTime timeStep
    ) {

        CommunityEnergyMetrics metrics = new CommunityEnergyMetrics();
        metrics.setTimestamp(timeStep);
        metrics.setTotalProduction(totalProduction);
        metrics.setTotalDemand(totalDemand);

        if (totalProduction < totalDemand) {
            double totalGridEnergy = totalDemand - totalProduction;
            metrics.setTotalGridEnergy(totalGridEnergy);
            metrics.setTotalSurplus(0.0);
        } else if (totalProduction > totalDemand) {
            metrics.setTotalGridEnergy(0.0);
            metrics.setTotalSurplus(totalProduction - totalDemand);
        } else {
            metrics.setTotalGridEnergy(0.0);
            metrics.setTotalSurplus(0.0);
        }

        return metrics;
    }

}
