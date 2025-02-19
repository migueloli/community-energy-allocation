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
public class CommunityEnergyResponseDTO {
    private String contributorId;
    private double availableEnergy;
    private LocalDateTime timestamp;
}
