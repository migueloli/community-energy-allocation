package com.ilo.energyallocation.energy.mapper;

import com.ilo.energyallocation.energy.dto.EnergyProductionRequestDTO;
import com.ilo.energyallocation.energy.dto.EnergyProductionResponseDTO;
import com.ilo.energyallocation.energy.model.EnergyProduction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnergyProductionMapper {
    @Mapping(source = "energyType", target = "type")
    @Mapping(source = "production", target = "amount")
    EnergyProduction toEntity(EnergyProductionRequestDTO request);

    @Mapping(source = "type", target = "energyType")
    @Mapping(source = "amount", target = "production")
    EnergyProductionResponseDTO toResponse(EnergyProduction production);

    List<EnergyProductionResponseDTO> toResponseList(List<EnergyProduction> productions);
}