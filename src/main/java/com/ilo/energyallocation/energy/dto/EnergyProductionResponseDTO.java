package com.ilo.energyallocation.energy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class EnergyProductionResponseDTO {
    private String id;
    private String userId;
    private EnergyProducedDTO producedEnergy;
    private double consumedEnergy;
    private EnergyAvailableDTO availableEnergy;
    private LocalDateTime timestamp;
}
