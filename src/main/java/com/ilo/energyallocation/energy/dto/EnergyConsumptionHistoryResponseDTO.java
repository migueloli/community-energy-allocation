package com.ilo.energyallocation.energy.dto;

import com.ilo.energyallocation.energy.model.EnergySource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnergyConsumptionHistoryResponseDTO {
    private String id;
    private String userId;
    private double requestedEnergy;
    private String strategyUsed;
    @Builder.Default
    private List<EnergySource> sourcesUsed = new ArrayList<>();
    private LocalDateTime timestamp;
}
