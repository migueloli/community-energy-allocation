package com.ilo.energyallocation.energy.dto;

import com.ilo.energyallocation.energy.model.EnergySource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnergyConsumptionResponseDTO {
    private double energyConsumed;
    @Builder.Default
    private List<EnergySource> sourcesUsed = new ArrayList<>();
    private double totalCost;

    public void addEnergySource(EnergySource source) {
        if (source == null) {
            return;
        }
        sourcesUsed.add(source);
    }

    public void addEnergySourceList(List<EnergySource> sources) {
        if (sources == null || sources.isEmpty()) {
            return;
        }
        sourcesUsed.addAll(sources);
    }
}
