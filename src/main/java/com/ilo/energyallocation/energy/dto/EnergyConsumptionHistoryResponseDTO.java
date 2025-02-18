package com.ilo.energyallocation.energy.dto;

import com.ilo.energyallocation.energy.model.EnergySource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class EnergyConsumptionHistoryResponseDTO {
    private String id;
    private String userId;
    private double requestedEnergy;
    private String strategyUsed;
    private List<EnergySource> sourcesUsed;
    private LocalDateTime timestamp;
}
