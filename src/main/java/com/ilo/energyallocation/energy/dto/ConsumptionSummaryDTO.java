package com.ilo.energyallocation.energy.dto;

import com.ilo.energyallocation.energy.model.EnergySource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ConsumptionSummaryDTO {
    private LocalDateTime startTime;
    private LocalDateTime stopTime;
    private double totalDemand;
    private List<EnergySource> allocation;
}
