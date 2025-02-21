package com.ilo.energyallocation.energy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "energy_consumption")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnergyConsumption {
    @Id
    private String id;
    private String userId;
    private double amount;
    private LocalDateTime timestamp;
    private double localEnergyAllocated;
    private double gridEnergyAllocated;
}
