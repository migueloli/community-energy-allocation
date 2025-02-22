package com.ilo.energyallocation.energy.service.interfaces;

import com.ilo.energyallocation.energy.model.EnergyType;

import java.time.LocalDateTime;
import java.util.Map;

public interface IRemainingEnergyTrackingService {
    void initializeTimeSlot(
            LocalDateTime time, Map<EnergyType, Double> initialProduction, Map<EnergyType, Double> initialDemand);

    Map<EnergyType, Double> getRemainingEnergy(LocalDateTime time);

    void consumeEnergy(EnergyType type, double amount, LocalDateTime time);

    double getRemainingProduction(LocalDateTime time, EnergyType type);

    double getRemainingDemand(LocalDateTime time, EnergyType type);

    LocalDateTime processTimeSlot(LocalDateTime timeSlot);
}
