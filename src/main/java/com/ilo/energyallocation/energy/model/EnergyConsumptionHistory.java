package com.ilo.energyallocation.energy.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "energy_consumption")
public class EnergyConsumptionHistory {
    @Id
    private String id;
    private String userId;
    private double requestedEnergy;
    private String strategyUsed;
    private List<EnergySource> sourcesUsed;
    private LocalDateTime timestamp;
}
