package com.ilo.energyallocation.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Password change request")
public class ChangePasswordRequestDTO {
    @Schema(description = "Current password", example = "currentPassword123")
    @NotBlank(message = "Current password is required")
    @Size(min = 8, message = "Current password must be at least 8 characters long")
    private String currentPassword;

    @Schema(description = "New password", example = "newPassword123")
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "New password must be at least 8 characters long")
    private String newPassword;
}
