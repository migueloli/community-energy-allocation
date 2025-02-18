package com.ilo.energyallocation.energy.service;

import com.ilo.energyallocation.common.exception.ResourceNotFoundException;
import com.ilo.energyallocation.energy.dto.EnergyProducedDTO;
import com.ilo.energyallocation.energy.dto.EnergyProductionRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyProductionResponseDTO;
import com.ilo.energyallocation.energy.mapper.EnergyProductionMapper;
import com.ilo.energyallocation.energy.model.EnergyAvailable;
import com.ilo.energyallocation.energy.model.EnergyProduction;
import com.ilo.energyallocation.energy.repository.EnergyProductionRepository;
import com.ilo.energyallocation.energy.service.interfaces.IEnergyProductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnergyProductionService implements IEnergyProductionService {
    private final EnergyProductionRepository productionRepository;
    private final EnergyProductionMapper productionMapper;

    @Override
    public EnergyProductionResponseDTO logProduction(String userId, EnergyProductionRequestDTO request) {
        EnergyProduction production = productionMapper.toEntity(request);
        production.setUserId(userId);
        production.setConsumedEnergy(0.0);
        production.setEnergyAvailable(calculateAvailableEnergy(request.getProducedEnergy()));

        return productionMapper.toResponse(productionRepository.save(production));
    }

    @Override
    public List<EnergyProductionResponseDTO> getProductionHistory(String userId) {
        List<EnergyProduction> history = productionRepository.findByUserIdOrderByTimestampDesc(userId);
        if (history.isEmpty()) {
            throw new ResourceNotFoundException("No production history found for user");
        }
        return productionMapper.toResponseList(history);
    }

    private EnergyAvailable calculateAvailableEnergy(EnergyProducedDTO produced) {
        EnergyAvailable available = new EnergyAvailable();
        available.setSolar(produced.getSolar());
        available.setWind(produced.getWind());
        available.setHydro(produced.getHydro());
        available.setBiomass(produced.getBiomass());
        return available;
    }
}
