package com.ilo.energyallocation.energy.dto;

import com.ilo.energyallocation.energy.model.EnergyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
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
    private EnergyType energyType;

    @NotNull
    @Min(0)
    private Double production;

    @Schema(description = "Timestamp of energy production", example = "2024-02-20T15:30:00Z")
    @NotNull(message = "Timestamp is required")
    @PastOrPresent(message = "Timestamp must be in the past or present")
    private LocalDateTime timestamp;
}
