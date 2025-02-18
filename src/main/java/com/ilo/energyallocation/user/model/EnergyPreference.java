package com.ilo.energyallocation.user.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User's preferred energy source for consumption")
public enum EnergyPreference {
    @Schema(description = "Solar energy preference", example = "SOLAR")
    SOLAR,

    @Schema(description = "Wind energy preference", example = "WIND")
    WIND,

    @Schema(description = "Hydroelectric energy preference", example = "HYDRO")
    HYDRO,

    @Schema(description = "Biomass energy preference", example = "BIOMASS")
    BIOMASS,

    @Schema(description = "No specific energy source preference", example = "NO_PREFERENCE")
    NO_PREFERENCE
}
