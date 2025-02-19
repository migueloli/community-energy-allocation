package com.ilo.energyallocation.energy.service;

import com.ilo.energyallocation.energy.dto.EnergyConsumptionHistoryRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionHistoryResponseDTO;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionResponseDTO;
import com.ilo.energyallocation.energy.mapper.ConsumptionHistoryMapper;
import com.ilo.energyallocation.energy.model.EnergyConsumptionHistory;
import com.ilo.energyallocation.energy.repository.EnergyConsumptionHistoryRepository;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyConsumptionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnergyConsumptionHistoryService implements IEnergyConsumptionHistoryService {
    private final EnergyConsumptionHistoryRepository energyConsumptionHistoryRepository;
    private final ConsumptionHistoryMapper consumptionHistoryMapper;

    @Override
    public List<EnergyConsumptionHistoryResponseDTO> getUserLogs(String userId) {
        return consumptionHistoryMapper.toResponseList(
                energyConsumptionHistoryRepository.findByUserIdOrderByTimestampDesc(userId)
        );
    }

    @Override
    public List<EnergyConsumptionHistoryResponseDTO> getUserLogsByPeriod(
            String userId, EnergyConsumptionHistoryRequestDTO request) {
        return consumptionHistoryMapper.toResponseList(
                energyConsumptionHistoryRepository.findByUserIdAndTimestampBetween(
                        userId,
                        request.getStartDate(),
                        request.getEndDate()
                )
        );
    }

    @Override
    public void logConsumption(
            String userId, double requestedAmount, EnergyConsumptionResponseDTO result, LocalDateTime timestamp) {
        EnergyConsumptionHistory log = new EnergyConsumptionHistory();
        log.setUserId(userId);
        log.setRequestedEnergy(requestedAmount);
        log.setStrategyUsed(result.getStrategyUsed());
        log.setSourcesUsed(result.getSourcesUsed());
        log.setTimestamp(LocalDateTime.now());

        energyConsumptionHistoryRepository.save(log);
    }
}
