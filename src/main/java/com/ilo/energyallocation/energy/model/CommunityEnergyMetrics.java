package com.ilo.energyallocation.energy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "community_energy_metrics")
public class CommunityEnergyMetrics {
    @Id
    private String id;
    private LocalDateTime timestamp;
    private double totalProduction;
    private double totalDemand;
    private double totalGridEnergy;
    private double totalSurplus;
}
