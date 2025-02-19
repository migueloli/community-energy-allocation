package com.ilo.energyallocation.energy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Energy consumption request")
public class EnergyConsumptionRequestDTO {
    @Schema(description = "Amount of energy requested for consumption", example = "10.5")
    @Positive(message = "Required amount must be positive")
    private double requiredAmount;

    @Schema(description = "Timestamp of consumption request", example = "2024-02-20T15:30:00Z")
    @NotNull(message = "Timestamp is required")
    @PastOrPresent(message = "Timestamp must be in the past or present")
    private LocalDateTime timestamp;
}
