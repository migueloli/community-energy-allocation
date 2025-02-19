package com.ilo.energyallocation.energy.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnergyAvailableDTO {
    @PositiveOrZero(message = "Solar energy must be zero or positive")
    private double solar;

    @PositiveOrZero(message = "Wind energy must be zero or positive")
    private double wind;

    @PositiveOrZero(message = "Hydro energy must be zero or positive")
    private double hydro;

    @PositiveOrZero(message = "Biomass energy must be zero or positive")
    private double biomass;
}
