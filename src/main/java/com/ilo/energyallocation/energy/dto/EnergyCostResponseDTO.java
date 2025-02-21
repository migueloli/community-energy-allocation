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
public class EnergyCostResponseDTO {
    private double solarCost;
    private double windCost;
    private double hydroCost;
    private double biomassCost;
    private double gridCost;
    private LocalDateTime timestamp;
}
