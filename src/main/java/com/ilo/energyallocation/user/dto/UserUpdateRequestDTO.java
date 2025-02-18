package com.ilo.energyallocation.user.dto;

import com.ilo.energyallocation.user.model.EnergyPreference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User profile update request")
public class UserUpdateRequestDTO {
    @Schema(description = "New email address", example = "new.email@example.com")
    private String email;

    @Schema(description = "Updated energy preferences configuration")
    private EnergyPreference preference;
}