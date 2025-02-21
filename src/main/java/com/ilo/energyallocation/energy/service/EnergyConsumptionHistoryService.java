package com.ilo.energyallocation.energy.service;

import com.ilo.energyallocation.energy.dto.EnergyConsumptionHistoryResponseDTO;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionResponseDTO;
import com.ilo.energyallocation.energy.mapper.ConsumptionHistoryMapper;
import com.ilo.energyallocation.energy.model.EnergyConsumptionHistory;
import com.ilo.energyallocation.energy.model.EnergySource;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.EnergyConsumptionRepository;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyConsumptionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnergyConsumptionHistoryService implements IEnergyConsumptionHistoryService {
    private final EnergyConsumptionRepository energyConsumptionHistoryRepository;
    private final ConsumptionHistoryMapper consumptionHistoryMapper;
    private final RemainingEnergyTrackingService remainingEnergyService;

    @Override
    public List<EnergyConsumptionHistoryResponseDTO> getUserLogs(String userId) {
        return consumptionHistoryMapper.toResponseList(
                energyConsumptionHistoryRepository.findByUserIdOrderByTimestampDesc(userId)
        );
    }

    @Override
    public List<EnergyConsumptionHistoryResponseDTO> getUserLogsByPeriod(
            String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return consumptionHistoryMapper.toResponseList(
                energyConsumptionHistoryRepository.findByUserIdAndTimestampBetween(
                        userId,
                        startDate,
                        endDate
                )
        );
    }

    @Override
    public void logConsumption(
            String userId, double requestedAmount, EnergyConsumptionResponseDTO result, LocalDateTime timestamp) {
        EnergyConsumptionHistory log = EnergyConsumptionHistory.builder()
                .userId(userId)
                .amount(requestedAmount)
                .timestamp(timestamp)
                .localEnergyAllocated(result.getEnergyConsumed())
                .gridEnergyAllocated(calculateGridEnergy(result.getSourcesUsed()))
                .build();

        energyConsumptionHistoryRepository.save(log);
    }

    private double calculateGridEnergy(List<EnergySource> sources) {
        return sources.stream()
                .filter(source -> source.getSource() == EnergyType.GRID)
                .mapToDouble(EnergySource::getAmount)
                .sum();
    }
}
