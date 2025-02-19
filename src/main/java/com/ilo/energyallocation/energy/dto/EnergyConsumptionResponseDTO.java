package com.ilo.energyallocation.energy.dto;

import com.ilo.energyallocation.energy.model.EnergySource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnergyConsumptionResponseDTO {
    private double energyConsumed;
    private List<EnergySource> sourcesUsed;
    private double totalCost;
    private String strategyUsed;
}
