package com.ilo.energyallocation.energy.mapper;

import com.ilo.energyallocation.energy.dto.EnergyAvailableDTO;
import com.ilo.energyallocation.energy.dto.EnergyProducedDTO;
import com.ilo.energyallocation.energy.dto.EnergyProductionRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyProductionResponseDTO;
import com.ilo.energyallocation.energy.model.EnergyAvailable;
import com.ilo.energyallocation.energy.model.EnergyProduced;
import com.ilo.energyallocation.energy.model.EnergyProduction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnergyProductionMapper {
    EnergyProduction toEntity(EnergyProductionRequestDTO request);

    EnergyProducedDTO toProducedEnergyDTO(EnergyProduced energyProduced);

    EnergyAvailableDTO toAvailableEnergyDTO(EnergyAvailable energyAvailable);

    @Mapping(target = "producedEnergy", source = "producedEnergy")
    @Mapping(target = "availableEnergy", source = "availableEnergy")
    EnergyProductionResponseDTO toResponse(EnergyProduction production);

    List<EnergyProductionResponseDTO> toResponseList(List<EnergyProduction> productions);
}