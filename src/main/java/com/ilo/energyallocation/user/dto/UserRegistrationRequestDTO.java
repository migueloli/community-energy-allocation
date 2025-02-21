package com.ilo.energyallocation.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ilo.energyallocation.user.model.EnergyPreference;
import com.ilo.energyallocation.user.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration request")
public class UserRegistrationRequestDTO {
    @Schema(description = "Desired username", example = "john.doe")
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(description = "Valid email address", example = "john.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(description = "Account password", example = "strongPassword123")
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @Schema(description = "User roles", example = "[\"USER\", \"ADMIN\"]")
    @JsonIgnore
    private List<Role> roles;

    @Schema(description = "Energy preferences configuration")
    private EnergyPreference preference;
}