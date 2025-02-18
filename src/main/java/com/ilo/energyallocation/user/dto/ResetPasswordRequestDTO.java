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
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Password reset request")
public class ResetPasswordRequestDTO {
    @Schema(description = "Password reset token received via email", example = "abc123def456")
    @NotBlank(message = "Token is required")
    private String token;

    @Schema(description = "New password", example = "newStrongPassword123")
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;
}