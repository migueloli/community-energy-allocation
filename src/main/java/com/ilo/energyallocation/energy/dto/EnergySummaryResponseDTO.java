package com.ilo.energyallocation.energy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Schema(description = "Energy consumption and production summary for a period")
public class EnergySummaryResponseDTO {
    private ConsumptionSummaryDTO consumption;
    private ProductionSummaryDTO production;
}
