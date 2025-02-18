package com.ilo.energyallocation.energy.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "energy_production")
public class EnergyProduction {
    @Id
    private String id;
    private String userId;
    private EnergyProduced energyProduced;
    private double consumedEnergy;
    private EnergyAvailable energyAvailable;
    private LocalDateTime timestamp;
}
