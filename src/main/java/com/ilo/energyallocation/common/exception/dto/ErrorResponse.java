package com.ilo.energyallocation.common.exception.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErrorResponse", description = "Standard error response structure")
public record ErrorResponse(
        @Schema(description = "HTTP status code", example = "400") int status,
        @Schema(description = "Error message details", example = "Invalid input provided") String message

) {
}
