package com.ilo.energyallocation.energy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnergyProductionResponseDTO {
    private String id;
    private String userId;
    private EnergyProducedDTO energyProduced;
    private double consumedEnergy;
    private EnergyAvailableDTO energyAvailable;
    private LocalDateTime timestamp;
}
