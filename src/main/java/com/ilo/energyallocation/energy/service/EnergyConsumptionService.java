package com.ilo.energyallocation.energy.service;

import com.ilo.energyallocation.common.exception.ValidationException;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyConsumptionResponseDTO;
import com.ilo.energyallocation.energy.model.EnergyAvailable;
import com.ilo.energyallocation.energy.model.EnergyProduction;
import com.ilo.energyallocation.energy.model.EnergySource;
import com.ilo.energyallocation.energy.repository.EnergyProductionRepository;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyConsumptionHistoryService;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyConsumptionService;
import com.ilo.energyallocation.energy.strategy.factory.EnergyConsumptionStrategyFactory;
import com.ilo.energyallocation.user.model.IloUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EnergyConsumptionService implements IEnergyConsumptionService {
    private final EnergyConsumptionStrategyFactory strategyFactory;
    private final EnergyProductionRepository productionRepository;
    private final IEnergyConsumptionHistoryService consumptionLogService;

    @Override
    public EnergyConsumptionResponseDTO consumeEnergy(EnergyConsumptionRequestDTO request, IloUser user) {
        LocalDateTime timestamp = LocalDateTime.now();

        EnergyConsumptionResponseDTO result = strategyFactory.getStrategiesInPriorityOrder().stream()
                .map(strategy -> strategy.consumeEnergy(request.getRequiredAmount(), user))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new ValidationException("No available energy strategy found"));

        updateProductionRecords(user.getId(), result);
        consumptionLogService.logConsumption(user.getId(), request.getRequiredAmount(), result, request.getTimestamp());

        return result;
    }


    private void updateProductionRecords(String userId, EnergyConsumptionResponseDTO consumption) {
        List<EnergyProduction> productions = productionRepository.findByUserIdOrderByTimestampDesc(userId);
        if (!productions.isEmpty()) {
            EnergyProduction latestProduction = productions.getFirst();
            updateAvailableEnergy(latestProduction, consumption.getSourcesUsed());
            latestProduction.setConsumedEnergy(latestProduction.getConsumedEnergy() + consumption.getEnergyConsumed());
            productionRepository.save(latestProduction);
        }
    }

    private void updateAvailableEnergy(EnergyProduction production, List<EnergySource> sourcesUsed) {
        EnergyAvailable available = production.getEnergyAvailable();
        for (EnergySource source : sourcesUsed) {
            switch (source.getSource()) {
                case SOLAR -> available.setSolar(available.getSolar() - source.getAmount());
                case WIND -> available.setWind(available.getWind() - source.getAmount());
                case HYDRO -> available.setHydro(available.getHydro() - source.getAmount());
                case BIOMASS -> available.setBiomass(available.getBiomass() - source.getAmount());
            }
        }
    }
}