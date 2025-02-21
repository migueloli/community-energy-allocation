package com.ilo.energyallocation.energy.service;

import com.ilo.energyallocation.common.exception.ResourceNotFoundException;
import com.ilo.energyallocation.common.exception.ValidationException;
import com.ilo.energyallocation.energy.dto.EnergyProductionRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyProductionResponseDTO;
import com.ilo.energyallocation.energy.mapper.EnergyProductionMapper;
import com.ilo.energyallocation.energy.model.EnergyProduction;
import com.ilo.energyallocation.energy.model.EnergyType;
import com.ilo.energyallocation.energy.repository.EnergyProductionRepository;
import com.ilo.energyallocation.energy.service.interfaces.IDemandCalculationService;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyProductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EnergyProductionService implements IEnergyProductionService {
    private final EnergyProductionRepository productionRepository;
    private final EnergyProductionMapper productionMapper;
    private final RemainingEnergyTrackingService remainingEnergyService;
    private final IDemandCalculationService demandCalculationService;

    @Override
    public EnergyProductionResponseDTO logProduction(String userId, EnergyProductionRequestDTO request) {
        validateRequest(request);

        EnergyProduction production = productionMapper.toEntity(request);
        production.setUserId(userId);
        production.setTimestamp(request.getTimestamp());

        // Save production record
        EnergyProduction savedProduction = productionRepository.save(production);

        // Initialize or update remaining energy for this time slot
        LocalDateTime timeSlot = request.getTimestamp().truncatedTo(ChronoUnit.MINUTES)
                .withMinute((request.getTimestamp().getMinute() / 15) * 15);

        Map<EnergyType, Double> initialProduction = new EnumMap<>(EnergyType.class);
        initialProduction.put(production.getType(), production.getAmount());

        Map<EnergyType, Double> initialDemand = new EnumMap<>(EnergyType.class);
        initialDemand.put(production.getType(), 0.0);

        remainingEnergyService.initializeTimeSlot(timeSlot, initialProduction, initialDemand);

        // Trigger cost recalculation
        demandCalculationService.updateEnergyCosts(timeSlot);

        return productionMapper.toResponse(savedProduction);
    }

    private void validateRequest(EnergyProductionRequestDTO request) {
        if (request.getTimestamp() == null) {
            throw new ValidationException("Timestamp is required");
        }
        if (request.getTimestamp().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Timestamp cannot be in the future");
        }
    }

    @Override
    public List<EnergyProductionResponseDTO> getProductionHistory(String userId) {
        List<EnergyProduction> history = productionRepository.findByUserIdOrderByTimestampDesc(userId);
        if (history.isEmpty()) {
            throw new ResourceNotFoundException("No production history found for user");
        }
        return productionMapper.toResponseList(history);
    }
}