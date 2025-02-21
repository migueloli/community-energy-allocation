package com.ilo.energyallocation.energy.service;

import com.ilo.energyallocation.common.exception.ValidationException;
import com.ilo.energyallocation.energy.dto.ConsumptionSummaryDTO;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionHistoryResponseDTO;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionResponseDTO;
import com.ilo.energyallocation.energy.dto.EnergySummaryResponseDTO;
import com.ilo.energyallocation.energy.dto.ProductionSummaryDTO;
import com.ilo.energyallocation.energy.model.EnergyProduction;
import com.ilo.energyallocation.energy.model.EnergySource;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.EnergyProductionRepository;
import com.ilo.energyallocation.energy.service.interfaces.IDemandCalculationService;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyConsumptionHistoryService;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyConsumptionService;
import com.ilo.energyallocation.energy.strategy.factory.EnergyConsumptionStrategyFactory;
import com.ilo.energyallocation.user.model.IloUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EnergyConsumptionService implements IEnergyConsumptionService {
    private final EnergyConsumptionStrategyFactory strategyFactory;
    private final EnergyProductionRepository productionRepository;
    private final IEnergyConsumptionHistoryService consumptionLogService;
    private final RemainingEnergyTrackingService remainingEnergyService;
    private final IDemandCalculationService demandCalculationService;

    @Override
    public EnergyConsumptionResponseDTO consumeEnergy(EnergyConsumptionRequestDTO request, IloUser user) {
        LocalDateTime timeSlot = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
                .withMinute((LocalDateTime.now().getMinute() / 15) * 15);

        double remainingAmount = request.getRequiredAmount();
        EnergyConsumptionResponseDTO result = null;

        // Initialize remaining energy if not already done
        initializeRemainingEnergy(timeSlot);

        for (var strategy : strategyFactory.getStrategiesInPriorityOrder()) {
            if (remainingAmount <= 0) break;

            var consumption = strategy.consumeEnergy(remainingAmount, user);
            if (consumption != null) {
                if (result == null) {
                    result = consumption;
                } else {
                    result.addEnergySourceList(consumption.getSourcesUsed());
                    result.setEnergyConsumed(result.getEnergyConsumed() + consumption.getEnergyConsumed());
                    result.setTotalCost(result.getTotalCost() + consumption.getTotalCost());
                }
                remainingAmount -= consumption.getEnergyConsumed();
            }
        }

        if (result == null) {
            throw new ValidationException("No available energy strategy found");
        }

        consumptionLogService.logConsumption(user.getId(), request.getRequiredAmount(), result, timeSlot);

        return result;
    }

    @Override
    public EnergySummaryResponseDTO getEnergySummary(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<EnergyConsumptionHistoryResponseDTO> consumptionHistory = consumptionLogService.getUserLogsByPeriod(
                userId, startDate, endDate);
        List<EnergyProduction> productionHistory = productionRepository.findByTimestampBetween(startDate, endDate);

        List<EnergySource> allocation = calculateAllocation(consumptionHistory);
        List<EnergySource> production = calculateProduction(productionHistory);
        double totalDemand = calculateTotalDemand(consumptionHistory);
        double surplus = calculateSurplus(production, totalDemand);

        return EnergySummaryResponseDTO.builder()
                .consumption(ConsumptionSummaryDTO.builder()
                        .startTime(startDate)
                        .stopTime(endDate)
                        .totalDemand(totalDemand)
                        .allocation(allocation)
                        .build())
                .production(ProductionSummaryDTO.builder()
                        .production(production)
                        .surplus(surplus)
                        .build())
                .build();
    }

    private void initializeRemainingEnergy(LocalDateTime timeSlot) {
        Map<EnergyType, Double> initialProduction = new EnumMap<>(EnergyType.class);
        Map<EnergyType, Double> initialDemand = new EnumMap<>(EnergyType.class);

        Arrays.stream(EnergyType.values()).forEach(type -> {
            initialProduction.put(
                    type, productionRepository.sumProductionByTypeAndTimestamp(type, timeSlot).orElse(0.0));
            initialDemand.put(type, 0.0);
        });

        remainingEnergyService.initializeTimeSlot(timeSlot, initialProduction, initialDemand);
    }

    private List<EnergySource> calculateAllocation(List<EnergyConsumptionHistoryResponseDTO> history) {
        return history.stream()
                .flatMap(record -> record.getSourcesUsed().stream())
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(
                                EnergySource::getSource,
                                Collectors.summingDouble(EnergySource::getAmount)
                        ),
                        map -> map.entrySet().stream()
                                .map(e -> new EnergySource(e.getKey(), e.getValue()))
                                .toList()
                ));
    }

    private List<EnergySource> calculateProduction(List<EnergyProduction> history) {
        return history.stream()
                .map(production -> new AbstractMap.SimpleEntry<>(production.getType(), production.getAmount()))
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.summingDouble(Map.Entry::getValue)
                        ),
                        map -> map.entrySet().stream()
                                .map(e -> new EnergySource(e.getKey(), e.getValue()))
                                .toList()
                ));
    }

    private double calculateTotalDemand(List<EnergyConsumptionHistoryResponseDTO> history) {
        return history.stream()
                .mapToDouble(EnergyConsumptionHistoryResponseDTO::getRequestedEnergy)
                .sum();
    }

    private double calculateSurplus(List<EnergySource> production, double totalDemand) {
        double totalProduction = production.stream().mapToDouble(EnergySource::getAmount).sum();
        return Math.max(0, totalProduction - totalDemand);
    }
}