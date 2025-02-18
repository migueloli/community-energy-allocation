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
@Schema(description = "Community energy sharing request")
public class CommunityEnergyShareRequestDTO {
    @Schema(description = "Amount of energy to share with community", example = "5.0")
    @Positive(message = "Energy amount must be positive")
    private double energyAmount;
}
