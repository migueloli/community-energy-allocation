package com.ilo.energyallocation.energy.mapper;

import com.ilo.energyallocation.energy.dto.CommunityEnergyResponseDTO;
import com.ilo.energyallocation.energy.model.CommunityEnergy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommunityEnergyMapper {
    CommunityEnergyResponseDTO toResponse(CommunityEnergy communityEnergy);

    List<CommunityEnergyResponseDTO> toResponseList(List<CommunityEnergy> communityEnergies);
}
