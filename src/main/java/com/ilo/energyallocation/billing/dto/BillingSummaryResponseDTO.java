package com.ilo.energyallocation.billing.dto;

import com.ilo.energyallocation.energy.model.EnergyType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class BillingSummaryResponseDTO {
    private String userId;
    private Map<EnergyType, EnergyUsageDTO> usageByType;
    private double totalCost;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
