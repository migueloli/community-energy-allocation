package com.ilo.energyallocation.energy.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "energy_costs")
public class EnergyCost {
    @Id
    private String id;
    private EnergyType type;
    private double cost;
    private LocalDateTime lastUpdated;
}
