package com.ilo.energyallocation.energy.dto;

import com.ilo.energyallocation.energy.model.EnergyType;
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
    private EnergyType energyType;
    private double production;
    private LocalDateTime timestamp;
}
