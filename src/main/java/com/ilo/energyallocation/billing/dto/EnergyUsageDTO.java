package com.ilo.energyallocation.billing.dto;

import lombok.Data;

@Data
public class EnergyUsageDTO {
    private double amountConsumed;
    private double cost;
    private double averageRate;
}
