package com.ilo.energyallocation.energy.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnergyConsumptionHistoryRequestDTO {
    @NotNull(message = "Start date is required")
    @Past(message = "Start date must be in the past")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @PastOrPresent(message = "End date cannot be in the future")
    private LocalDateTime endDate;
}
