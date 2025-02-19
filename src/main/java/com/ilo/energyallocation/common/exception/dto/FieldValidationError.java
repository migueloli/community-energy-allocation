package com.ilo.energyallocation.common.exception.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FieldValidationError", description = "Field-specific validation error")
public record FieldValidationError(
        @Schema(description = "Field name", example = "email") String field,
        @Schema(description = "Error message", example = "must not be blank") String message
) {
}
