package com.ilo.energyallocation.energy.service.interfaces;

import com.ilo.energyallocation.energy.dto.EnergyConsumptionHistoryResponseDTO;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface IEnergyConsumptionHistoryService {
    List<EnergyConsumptionHistoryResponseDTO> getUserLogs(String userId);

    List<EnergyConsumptionHistoryResponseDTO> getUserLogsByPeriod(
            String userId, LocalDateTime startDate, LocalDateTime endDate);

    void logConsumption(
            String userId, double requestedAmount, EnergyConsumptionResponseDTO result, LocalDateTime timestamp);
}
