package com.ilo.energyallocation.billing.dto;

import com.ilo.energyallocation.energy.model.EnergyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Billing summary with detailed energy usage and costs")
public class BillingSummaryResponseDTO {
    @Schema(description = "User identifier")
    private String userId;

    @Schema(description = "Energy usage breakdown by source type")
    private Map<EnergyType, EnergyUsageDTO> usageByType;

    @Schema(description = "Total cost for the billing period", example = "150.75")
    private double totalCost;

    @Schema(description = "Start date of the billing period", example = "2024-01-01T00:00:00Z")
    private LocalDateTime startDate;

    @Schema(description = "End date of the billing period", example = "2024-01-31T23:59:59Z")
    private LocalDateTime endDate;
}
