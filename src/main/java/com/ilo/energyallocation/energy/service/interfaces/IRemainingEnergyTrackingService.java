package com.ilo.energyallocation.energy.service.interfaces;

import com.ilo.energyallocation.energy.model.EnergyType;

import java.time.LocalDateTime;
import java.util.Map;

public interface IRemainingEnergyTrackingService {
    void initializeTimeSlot(
            LocalDateTime timeSlot, Map<EnergyType, Double> initialProduction, Map<EnergyType, Double> initialDemand);

    Map<EnergyType, Double> getRemainingEnergy();

    void consumeEnergy(EnergyType type, double amount);

    double getRemainingProduction(LocalDateTime timeSlot, EnergyType type);

    double getRemainingDemand(LocalDateTime timeSlot, EnergyType type);
}
