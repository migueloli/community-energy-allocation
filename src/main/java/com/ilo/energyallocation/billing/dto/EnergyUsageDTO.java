package com.ilo.energyallocation.billing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Energy usage details for a specific source")
public class EnergyUsageDTO {
    @Schema(description = "Total amount of energy consumed", example = "125.5")
    private double amountConsumed;

    @Schema(description = "Total cost for the consumed energy", example = "45.25")
    private double cost;

    @Schema(description = "Average rate per unit of energy", example = "0.36")
    private double averageRate;
}
