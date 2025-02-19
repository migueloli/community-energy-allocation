package com.ilo.energyallocation.energy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Energy production request details")
public class EnergyProductionRequestDTO {
    @NotNull
    @Valid
    @Schema(description = "Details of energy produced from different sources")
    private EnergyProducedDTO producedEnergy;

    @Schema(description = "Timestamp of energy production", example = "2024-02-20T15:30:00Z")
    @NotNull(message = "Timestamp is required")
    @PastOrPresent(message = "Timestamp must be in the past or present")
    private LocalDateTime timestamp;
}
