package com.ilo.energyallocation.energy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Energy consumption request")
public class EnergyConsumptionRequestDTO {
    @Schema(description = "Amount of energy requested for consumption", example = "10.5")
    @Positive(message = "Required amount must be positive")
    private double requiredAmount;
}
