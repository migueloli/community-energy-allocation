package com.ilo.energyallocation.energy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "energy_consumption_history")
public class EnergyConsumptionHistory {
    @Id
    private String id;
    private String userId;
    private double amount;
    private LocalDateTime timestamp;
    @Builder.Default
    private List<EnergySource> sourcesUsed = new ArrayList<>();
    private double totalCost;
}
