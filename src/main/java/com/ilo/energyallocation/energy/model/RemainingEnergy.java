package com.ilo.energyallocation.energy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "remaining_energy")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemainingEnergy {
    @Id
    private String id;
    private LocalDateTime timeSlot;
    private EnergyType type;
    private double remainingProduction;
    private double remainingDemand;
}

