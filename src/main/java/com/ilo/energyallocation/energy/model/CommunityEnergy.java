package com.ilo.energyallocation.energy.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "community_energy_pool")
public class CommunityEnergy {
    @Id
    private String id;
    private double availableEnergy;
    private String contributorId;
    private LocalDateTime timestamp;
    private boolean isConsumed;
}
