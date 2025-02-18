package com.ilo.energyallocation.energy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Energy production request details")
public class EnergyProductionRequestDTO {
    @NotNull
    @Valid
    @Schema(description = "Details of energy produced from different sources")
    private EnergyProducedDTO producedEnergy;
}
