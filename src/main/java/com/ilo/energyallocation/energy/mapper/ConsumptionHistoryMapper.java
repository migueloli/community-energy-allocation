package com.ilo.energyallocation.energy.mapper;

import com.ilo.energyallocation.energy.dto.EnergyConsumptionHistoryResponseDTO;
import com.ilo.energyallocation.energy.model.EnergyConsumptionHistory;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConsumptionHistoryMapper {
    EnergyConsumptionHistoryResponseDTO toResponse(EnergyConsumptionHistory log);

    List<EnergyConsumptionHistoryResponseDTO> toResponseList(List<EnergyConsumptionHistory> logs);
}
