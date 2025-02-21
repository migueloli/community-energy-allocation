package com.ilo.energyallocation.energy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "energy_production")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnergyProduction {
    @Id
    private String id;
    private String userId;
    private EnergyType type;
    private double amount;
    private LocalDateTime timestamp;
}
