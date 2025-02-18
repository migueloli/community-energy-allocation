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
public class CommunityEnergyResponseDTO {
    private String contributorId;
    private double availableEnergy;
    private LocalDateTime timestamp;
}
