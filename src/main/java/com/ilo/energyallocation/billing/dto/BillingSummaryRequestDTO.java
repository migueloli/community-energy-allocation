package com.ilo.energyallocation.billing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request parameters for generating billing summary")
public class BillingSummaryRequestDTO {
    @Schema(description = "Start date for billing period", example = "2024-01-01T00:00:00Z")
    @NotNull(message = "Start date is required")
    @Past(message = "Start date must be in the past")
    private LocalDateTime startDate;

    @Schema(description = "End date for billing period", example = "2024-01-31T23:59:59Z")
    @NotNull(message = "End date is required")
    @PastOrPresent(message = "End date cannot be in the future")
    private LocalDateTime endDate;
}
