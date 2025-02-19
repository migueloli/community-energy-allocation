package com.ilo.energyallocation.common.exception.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "ValidationErrorResponse", description = "Validation error response structure")
public record ValidationErrorResponse(
        @Schema(description = "HTTP status code", example = "400") int status,
        @Schema(description = "List of field validation errors") List<FieldValidationError> errors
) {
}
