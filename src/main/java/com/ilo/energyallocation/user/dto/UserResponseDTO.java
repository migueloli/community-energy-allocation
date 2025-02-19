package com.ilo.energyallocation.user.dto;

import com.ilo.energyallocation.user.model.EnergyPreference;
import com.ilo.energyallocation.user.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User profile response data")
public class UserResponseDTO {
    @Schema(description = "Unique identifier", example = "507f1f77bcf86cd799439011")
    private String id;

    @Schema(description = "Username", example = "john.doe")
    private String username;

    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User roles", example = "[\"USER\", \"ADMIN\"]")
    private List<Role> roles;

    @Schema(description = "Energy preferences configuration")
    private EnergyPreference preference;
}
