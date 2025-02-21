package com.ilo.energyallocation.energy.dto;

import com.ilo.energyallocation.energy.model.EnergySource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnergyConsumptionResponseDTO {
    private double energyConsumed;
    private List<EnergySource> sourcesUsed;
    private double totalCost;

    public void addEnergySource(EnergySource source) {
        if (sourcesUsed == null) {
            sourcesUsed = Collections.synchronizedList(List.of(source));
            return;
        }
        sourcesUsed.add(source);
    }

    public void addEnergySourceList(List<EnergySource> sources) {
        if (sources == null) {
            return;
        }

        if (sourcesUsed == null) {
            sourcesUsed = Collections.synchronizedList(sources);
            return;
        }

        sourcesUsed.addAll(sources);
    }
}
